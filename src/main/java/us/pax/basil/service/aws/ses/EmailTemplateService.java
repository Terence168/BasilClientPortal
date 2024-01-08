package us.pax.basil.service.aws.ses;

import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

/**
 * Service for processing email templates.
 * Utilizes Thymeleaf template engine to merge a given data model into a specified template.
 */
@Service
public class EmailTemplateService {

    private final TemplateEngine templateEngine;

    /**
     * Constructs an EmailTemplateService with the provided Thymeleaf TemplateEngine.
     *
     * @param templateEngine the Thymeleaf TemplateEngine to use for processing templates.
     */
    public EmailTemplateService(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    /**
     * Builds the email content by merging the provided data model into the specified template.
     * The data model is a map where each key represents a variable name that will be used in the template,
     * and the corresponding value is the data that should replace that variable in the template.
     * <br><br>
     * Example: < span th:text="${userName}"> - include a map entry with "userName" as the key
     *
     * @param templateName the name of the Thymeleaf template to process. Template needs to
     *                     be in resources/templates and have the .html extension.
     * @param model        a map containing the variables to be merged into the template.
     *                     For example:
     *                     Map<String, Object> model = new HashMap<>();
     *                     model.put("userName", "Phillip");
     *                     model.put("signUpDate", LocalDate.now());
     *                     The keys should match the variable names used in the template.
     * @return a String containing the processed template with the data model applied. This string
     *         is the final copy of the email and can include HTML tags which will be rendered
     *         by the email client.
     */
    public String build(String templateName, Map<String, Object> model) {
        Context context = new Context();
        context.setVariables(model);
        return templateEngine.process(templateName, context);
    }
}