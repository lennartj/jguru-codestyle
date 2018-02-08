/**
 * Package holding entity classes defining Organisations in the context of the mithlond reactor.
 *
 * @since 1.0
 */
@XmlSchema(xmlns = {
        @XmlNs(prefix = "organisation", namespaceURI = "http://foo.bar.com/baz")}
)
@XmlAccessorType(XmlAccessType.FIELD)
package se.jguru.nazgul.tools.validation.api;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;