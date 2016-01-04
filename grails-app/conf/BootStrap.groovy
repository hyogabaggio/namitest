import com.nami.GenericDomain
import com.nami.Users
import com.nami.Utilities

class BootStrap {

    def init = { servletContext ->

        /*  def hyoga = new Users(
                  nom: "baggio",
                  prenom: "hyoga",
                  username: "hyoga",
                  email: "c.hyoga@gmail.com",
                  age: 0,
                  sexe: "H",
          ).save(failOnError: true)   */

        /**
         * 'Override' de la methode 'asType' de String.
         * 'asType' vérifie si un string est convertissable vers un type donné. Mais la méthode plante avec les dates.
         * Donc si le type donné est une date, on essaie d'abord de faire le parsing.
         * Si ce n'est pas une date, on effectue le 'asType' normal
         */
        def oldAsType = String.metaClass.getMetaMethod("asType", [Class] as Class[])
        String.metaClass.asType = { Class type ->
            type.isAssignableFrom(Date) ?
                    Date.parse("dd/MM/yyyy HH:mm:ss", delegate) :
                    oldAsType.invoke(delegate, [type] as Class[])
        }
        //todo penser à datetime =>
        /* String.metaClass.asType = { Class type ->
             type.isAssignableFrom(DateTime) ?
                     new DateTime(delegate) :
                     oldAsType.invoke(delegate, [type] as Class[])
         }*/

    }
    def destroy = {
    }
}
