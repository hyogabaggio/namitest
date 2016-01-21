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
     * exemple: 'http://localhost:8080/nami_gr_2_5/car/index?redir=aaaaaaaaaa&batch=pomme' affiche la page
     * 'http://localhost:8080/nami_gr_2_5/users/index?redir=XXXXX&batch=pomme'
     */
    void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httprequest = (HttpServletRequest) request;
        // on recupere l'uri. Ex: requestURI = /nami/users/create
        String requestURI = httprequest.getRequestURI()
        /*  if (!requestURI.contains("assets") && requestURI?.split('/')?.size() > 2) {
              System.out.println("requestURL = " + httprequest.getRequestURL())
              System.out.println("requestURI = " + httprequest.getRequestURI())
          }                              */
        String oldController = "users"
        if (!requestURI.contains("assets") && requestURI?.split('/')?.size() > 2/* && requestURI?.split('/')[2] != "users"*/) {
            // on recupere le controller saisi dans l'url
            oldController = requestURI?.split('/')[2]
           // System.out.println("oldController = " + oldController)
            //System.out.println("size = " + requestURI?.split('/')?.size())
            String action = requestURI?.split('/').size() > 3 ? requestURI?.split('/')[3] : "index"

            // on le place en attribut dans le request, afin de le recuperer et le manipuler dans le controller generique de destination
            request.setAttribute("redir", oldController)

            // on remplace le controller saisi dans l'url par le controller générique de destination, et on enleve le nom de l'application
            String newURI = "/users/" + action
            // System.out.println("newURI = " + newURI)
            RequestDispatcher requestDispatcher = request.getServletContext().getRequestDispatcher(newURI)
            requestDispatcher.forward(request, response)

        } else chain.doFilter(request, response)

        //TODO voir request.startAsync

    }
}
