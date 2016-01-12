package com.nami

import org.springframework.web.context.support.WebApplicationContextUtils

import javax.servlet.*
import javax.servlet.http.HttpServletRequest

/**
 * Created by hyoga on 08/01/2016.
 *
 * Filter, implementée façon java. Le filter groovy ne marche pas pour ce cas-ci
 * Voir le fichier web.xml pour l'integration (filter urlRewriteFilter)
 */
class UrlRewriteFilter implements Filter {
    def applicationContext

    void init(FilterConfig config) throws ServletException {
        applicationContext = WebApplicationContextUtils.getWebApplicationContext(config.servletContext)
    }

    void destroy() {
    }

    /**
     * redirect tous les controllers vers users, mais garde l'action originale.
     * exemple: 'http://localhost:8080/nami_gr_2_5/car/index?redir=aaaaaaaaaa&batch=pomme' affiche la page 'http://localhost:8080/nami_gr_2_5/users/index?redir=aaaaaaaaaa&batch=pomme'
     */
    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        // chain.doFilter(request, response)
        HttpServletRequest httprequest = (HttpServletRequest) request;
        String requestURI = httprequest.getRequestURI();
        if (!requestURI.contains("assets") && requestURI?.split('/')?.size() > 2) {
            //  System.out.println("requestURI = " + requestURI)

            String redirParam = requestURI?.split('/')[2]
            // System.out.println("redirParam = " + redirParam)
            request.setAttribute("redir", redirParam);
            String newURI = "/users"
            // RequestDispatcher requestDispatcher = request.getRequestDispatcher(newURI)
            RequestDispatcher requestDispatcher = request.getServletContext().getRequestDispatcher(newURI)
            requestDispatcher.forward(request, response)

        } else chain.doFilter(request, response)

        //TODO voir request.startAsync

    }
}
