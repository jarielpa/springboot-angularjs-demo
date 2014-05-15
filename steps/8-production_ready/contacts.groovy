package commands

import java.io.IOException
import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Required
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext
import org.springframework.beans.factory.BeanFactory
import com.kms.contact.service.ContactService

class contacts {

    @Usage("Load contacts from local file path")
    @Command
    def load(InvocationContext context, @Usage("The local file path") @Required @Argument String path) {
        BeanFactory factory = context.attributes["spring.beanfactory"]
        ContactService service = factory.getBean(ContactService.class)
        long count = service.loadContacts(path);
        
        return String.format("Loaded %d contacts into DB", count)
    }
    
    @Usage("Clear all contacts from DB")
    @Command
    def clear(InvocationContext context) {
        BeanFactory factory = context.attributes["spring.beanfactory"]
        ContactService service = factory.getBean(ContactService.class)
        service.deleteAllContacts()
    
        return "All contacts were deleted from DB"
    }

}