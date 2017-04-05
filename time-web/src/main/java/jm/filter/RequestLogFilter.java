package jm.filter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.TeeOutputStream;
import org.apache.commons.lang3.StringUtils;

import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.*;
import java.io.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by vk on 08.06.15.
 */
@WebFilter(filterName = "RequestLogFilter", urlPatterns = "/*")
public class RequestLogFilter implements Filter {

    @Inject Logger logger;

    public final static String REQUEST_UUID = "REQUEST_UUID";

    public void destroy() {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        if ("/freeddom/resource/status".equals(((HttpServletRequest)request).getRequestURI()) ||
                "/freeddom/".equals(((HttpServletRequest)request).getRequestURI())) {
            // ignore status resource logging (heartbeat)
            chain.doFilter(request, response);
            return;
        }

        if (request.getContentType() != null && request.getContentType().contains("multipart/")) {
            logger.info(((HttpServletRequest)request).getRequestURI() + ": Multipart request logging ignore");
            request.setCharacterEncoding("UTF-8");
            chain.doFilter(request, response);
            return;
        }
        if (request.getContentType() != null && request.getContentType().contains("application/x-www-form-urlencoded")) {
            logger.info(((HttpServletRequest)request).getRequestURI() + ": application/x-www-form-urlencoded will ignore");
            chain.doFilter(request, response);
            return;
        }
        if (((HttpServletRequest)request).getRequestURI().contains("png")
                || ((HttpServletRequest)request).getRequestURI().contains("js")
                || ((HttpServletRequest)request).getRequestURI().contains("css")
                || ((HttpServletRequest)request).getRequestURI().contains("gif")
                || ((HttpServletRequest)request).getRequestURI().contains("ttf.html")
                || ((HttpServletRequest)request).getRequestURI().contains("ico.html")
                || ((HttpServletRequest)request).getRequestURI().contains("woff")) {
            // ignore resource logging
            chain.doFilter(request, response);
            return;
        }

        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            HttpServletResponse httpServletResponse = (HttpServletResponse) response;

            BufferedRequestWrapper bufferedReqest = new BufferedRequestWrapper(httpServletRequest);
            BufferedResponseWrapper bufferedResponse = new BufferedResponseWrapper(httpServletResponse);

            String uuid = UUID.randomUUID().toString();

            logRequest(httpServletRequest, bufferedReqest, uuid);

            bufferedReqest.resetInputStream();

            long startTime = System.currentTimeMillis();

            chain.doFilter(bufferedReqest, bufferedResponse);
//            chain.doFilter(request, response);

            long timeElapsed = System.currentTimeMillis() - startTime;

            logResponse(httpServletRequest, bufferedResponse, uuid, timeElapsed);
        } catch (Throwable a) {
            logger.severe(a.getMessage());
        }
    }

    private void logRequest(HttpServletRequest httpServletRequest, BufferedRequestWrapper bufferedReqest, String uuid) throws IOException {
        Map<String, String> requestMap = this.getTypesafeRequestMap(httpServletRequest);
        String requestURI = httpServletRequest.getRequestURI();
        String encoding = httpServletRequest.getCharacterEncoding();

        final StringBuilder requestMessage = new StringBuilder("HTTP Request")
                .append(" [UUID: ").append(uuid)
                .append("]\n\t[HTTP METHOD: ").append(httpServletRequest.getMethod())
                .append("]\n\t[REQUEST URI: ").append(requestURI)
                .append("]\n\t[REMOTE ADDRESS: ").append(httpServletRequest.getRemoteAddr());
        if (!requestMap.isEmpty()) {
            requestMessage.append("]\n\t[REQUEST PARAMETERS: ").append(requestMap);
        }
        boolean multipartFormData = false;
        if (StringUtils.isNotEmpty(httpServletRequest.getContentType())) {
            requestMessage.append("]\n\t[REQUEST CONTENT-TYPE: ").append(httpServletRequest.getContentType());
            multipartFormData = httpServletRequest.getContentType().startsWith("multipart/form-data");
        }
        if (StringUtils.isNotEmpty(encoding)) {
            requestMessage.append("]\n\t[REQUEST CHARACTER-ENCODING: ").append(encoding);
        }

        if (!multipartFormData) {
            String requestBody = bufferedReqest.getRequestBody();
            if (StringUtils.isNotEmpty(requestBody)) {
                // Укорачиваем длинные реквесты
                if (requestBody.length() > 15000) {
                    logger.warning("Long request body for UUID[" + uuid + "]: " + requestBody);
                    requestBody = requestBody.substring(0, 100) + " ... " + requestBody.substring(requestBody.length() - 100, requestBody.length());
                }
                requestMessage.append("]\n\t[REQUEST BODY: ").append(requestBody);
            }
        }
        requestMessage.append("]");

        logger.info(requestMessage.toString());
    }

    private void logResponse(HttpServletRequest httpServletRequest, BufferedResponseWrapper bufferedResponse, String uuid, long timeElapsed) {
        String requestURI = httpServletRequest.getRequestURI();

        StringBuilder responseMessage = new StringBuilder("HTTP Response")
                .append(" [UUID: ").append(uuid)
                .append("]\n\t[REQUEST URI: ").append(requestURI)
                .append("]\n\t[TIME ELAPSED: ").append(timeElapsed).append(" ms");
        responseMessage.append("]\n\t[RESPONSE HTTP CODE: ").append(bufferedResponse.getStatus());
        if (StringUtils.isNotEmpty(bufferedResponse.getContentType())) {
            responseMessage.append("]\n\t[RESPONSE CONTENT-TYPE: ").append(bufferedResponse.getContentType());
        }
        String contentType = StringUtils.defaultIfEmpty(bufferedResponse.getContentType(), "");
        boolean printContent = true;
        if (requestURI.endsWith(".css.html")
                || requestURI.endsWith(".js.html")
                || requestURI.endsWith(".png.html")
                || requestURI.endsWith(".css")
                || requestURI.endsWith(".png")
                || requestURI.endsWith(".zip")
                || contentType.contains("image")
                || contentType.equals("application/octet-stream")
                || StringUtils.isEmpty(bufferedResponse.getContent())) {
            printContent = false;
        }
        if (printContent) {
            String responseBody = bufferedResponse.getContent();
            if (responseBody.length() > 15000) {
                logger.info("Long response body for UUID[" + uuid + "]: " + responseBody);
                responseBody = responseBody.substring(0, 100) + " ... " + responseBody.substring(responseBody.length() - 100, responseBody.length());
            }
            responseMessage.append("]\n\t[RESPONSE CONTENT: ").append(responseBody);
        }
        responseMessage.append("]");

        logger.info(responseMessage.toString());
    }

    public void init(FilterConfig config) throws ServletException {

    }

    private Map<String, String> getTypesafeRequestMap(HttpServletRequest request) {
        request.getParameterMap();
        Map<String, String> typesafeRequestMap = new HashMap<>();
        for (String key : request.getParameterMap().keySet()) {
            String[] value = request.getParameterMap().get(key);
            typesafeRequestMap.put(key, getString(value));
        }
        return typesafeRequestMap;
    }

    private String getString(String[] strings) {
        if (strings == null || strings.length == 0) {
            return "";
        } else {
            StringBuilder result = new StringBuilder();
            for (String s : strings) {
                result.append(s).append("|");
            }
            return result.toString();
        }
    }

    static final class BufferedRequestWrapper extends HttpServletRequestWrapper {

        private HttpServletRequest request;
        private ByteArrayInputStream bais = null;
        private BufferedServletInputStream servletInputStream = new BufferedServletInputStream();
        private byte[] buffer = null;


        public BufferedRequestWrapper(HttpServletRequest req) throws IOException {
            super(req);
            this.request = req;

            // Read InputStream and store its content in a buffer.
//            InputStream is = req.getInputStream();
//            this.baos = new ByteArrayOutputStream();
//            byte buf[] = new byte[1024];
//            int letti;
//            while ((letti = is.read(buf)) > 0) {
//                this.baos.write(buf, 0, letti);
//            }
//            this.buffer = this.baos.toByteArray();
        }

        public void resetInputStream() {
            if (buffer != null) {
                bais = new ByteArrayInputStream(buffer);
                servletInputStream = new BufferedServletInputStream(bais);
            }
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            if (buffer == null) {
                buffer = IOUtils.toByteArray(request.getInputStream());
                bais = new ByteArrayInputStream(buffer);
                servletInputStream = new BufferedServletInputStream(bais);
            }
            return servletInputStream;
        }

        @Override
        public BufferedReader getReader() throws IOException {
            if (buffer == null) {
                buffer = IOUtils.toByteArray(request.getInputStream());
                bais = new ByteArrayInputStream(buffer);
                servletInputStream = new BufferedServletInputStream(bais);
            }
            return new BufferedReader(new InputStreamReader(servletInputStream));
        }

        public String getRequestBody() throws IOException {
            return IOUtils.toString(getInputStream());
        }

    }

    private static final class BufferedServletInputStream extends ServletInputStream {

        private ByteArrayInputStream bais;

        public BufferedServletInputStream() {}

        @Override
        public boolean isFinished() {
            return false;
        }

        public BufferedServletInputStream(ByteArrayInputStream bais) {
            this.bais = bais;
        }

        @Override
        public int available() {
            if (bais != null) {
                return this.bais.available();
            } else {
                return -1;
            }
        }

        @Override
        public int read() {
            if (bais != null) {
                return this.bais.read();
            } else {
                return -1;
            }
        }

        @Override
        public int read(byte[] buf, int off, int len) {
            if (bais != null) {
                return this.bais.read(buf, off, len);
            } else {
                return -1;
            }
        }

        public boolean isReady() { return true; }

        @Override
        public void setReadListener(ReadListener readListener) {

        }

    }

    private static class TeeServletOutputStream extends ServletOutputStream {

        private final TeeOutputStream targetStream;

        public TeeServletOutputStream(OutputStream one, OutputStream two) {
            targetStream = new TeeOutputStream(one, two);
        }

        @Override
        public void write(int arg0) throws IOException {
            this.targetStream.write(arg0);
        }

        public void flush() throws IOException {
            super.flush();
            this.targetStream.flush();
        }

        public void close() throws IOException {
            super.close();
            this.targetStream.close();
        }

        public void setWriteListener(WriteListener var1) {}

        public boolean isReady() { return true; }

    }

    static class BufferedResponseWrapper extends HttpServletResponseWrapper {

        HttpServletResponse original;
        TeeServletOutputStream tee;
        ByteArrayOutputStream bos;

        BufferedResponseWrapper(HttpServletResponse response) {
            super(response);
            original = response;
        }

        public String getContent() {
            return (bos != null) ? bos.toString() : "";
        }

        public PrintWriter getWriter() throws IOException {
            return original.getWriter();
        }

        public ServletOutputStream getOutputStream() throws IOException {
            if (tee == null) {
                bos = new ByteArrayOutputStream();
                tee = new TeeServletOutputStream(original.getOutputStream(), bos);
            }
            return tee;

        }

        @Override
        public String getCharacterEncoding() {
            return original.getCharacterEncoding();
        }

        @Override
        public String getContentType() {
            return original.getContentType();
        }

        @Override
        public void setCharacterEncoding(String charset) {
            original.setCharacterEncoding(charset);
        }

        @Override
        public void setContentLength(int len) {
            original.setContentLength(len);
        }

        @Override
        public void setContentType(String type) {
            original.setContentType(type);
        }

        @Override
        public void setBufferSize(int size) {
            original.setBufferSize(size);
        }

        @Override
        public int getBufferSize() {
            return original.getBufferSize();
        }

        @Override
        public void flushBuffer() throws IOException {
            getOutputStream().flush();
        }

        @Override
        public void resetBuffer() {
            original.resetBuffer();
        }

        @Override
        public boolean isCommitted() {
            return original.isCommitted();
        }

        @Override
        public void reset() {
            original.reset();
        }

        @Override
        public void setLocale(Locale locale) {

        }

        @Override
        public Locale getLocale() {
            return original.getLocale();
        }

        @Override
        public void addCookie(Cookie cookie) {

        }

        @Override
        public boolean containsHeader(String name) {
            return original.containsHeader(name);
        }

        @Override
        public String encodeURL(String url) {
            return original.encodeURL(url);
        }

        @Override
        public String encodeRedirectURL(String url) {
            return original.encodeRedirectURL(url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public String encodeUrl(String url) {
            return original.encodeUrl(url);
        }

        @SuppressWarnings("deprecation")
        @Override
        public String encodeRedirectUrl(String url) {
            return original.encodeRedirectUrl(url);
        }

        @Override
        public void sendError(int sc, String msg) throws IOException {
            original.sendError(sc, msg);
        }

        @Override
        public void sendError(int sc) throws IOException {
            original.sendError(sc);
        }

        @Override
        public void sendRedirect(String location) throws IOException {
            if (!original.isCommitted()) {
                original.sendRedirect(location);
            }
        }

        @Override
        public void setDateHeader(String name, long date) {
            original.setDateHeader(name, date);
        }

        @Override
        public void addDateHeader(String name, long date) {
            original.addDateHeader(name, date);
        }

        @Override
        public void setHeader(String name, String value) {
            original.setHeader(name, value);
        }

        @Override
        public void addHeader(String name, String value) {
            original.addHeader(name, value);
        }

        @Override
        public void setIntHeader(String name, int value) {
            original.setIntHeader(name, value);
        }

        @Override
        public void addIntHeader(String name, int value) {
            original.addIntHeader(name, value);
        }

        @Override
        public void setStatus(int sc) {
            original.setStatus(sc);
        }

        @SuppressWarnings("deprecation")
        @Override
        public void setStatus(int sc, String sm) {
            original.setStatus(sc, sm);
        }


        public int getStatus() {
            return original.getStatus();
        }

        public Collection<String> getHeaderNames() {
            return new ArrayList<String>();
        }

        public String getHeader(String var1) {
            return original.getHeader(var1);
        }

        public Collection<String> getHeaders(String var1) {
            return original.getHeaders(var1);
        }

        public void setContentLengthLong(long var1) {}

    }
}


