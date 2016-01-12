class UrlMappings {

    static mappings = {
        "/$controller/$action?/$id?(.$format)?" {
            constraints {
                // apply constraints here
            }
        }

        // "/$controller/$action?/$id?(.$format)?"(controller: "users")
        "/"(view: "/index")
        "500"(view: '/error')
    }
}
