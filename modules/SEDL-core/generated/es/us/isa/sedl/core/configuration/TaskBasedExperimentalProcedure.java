//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2021.07.08 a las 10:27:28 AM CEST 
//


package es.us.isa.sedl.core.configuration;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.jvnet.jaxb2_commons.lang.CopyStrategy;
import org.jvnet.jaxb2_commons.lang.CopyTo;
import org.jvnet.jaxb2_commons.lang.Equals;
import org.jvnet.jaxb2_commons.lang.EqualsStrategy;
import org.jvnet.jaxb2_commons.lang.HashCode;
import org.jvnet.jaxb2_commons.lang.HashCodeStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBCopyStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBEqualsStrategy;
import org.jvnet.jaxb2_commons.lang.JAXBHashCodeStrategy;
import org.jvnet.jaxb2_commons.locator.ObjectLocator;
import org.jvnet.jaxb2_commons.locator.util.LocatorUtils;


/**
 * <p>Clase Java para TaskBasedExperimentalProcedure complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="TaskBasedExperimentalProcedure"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{http://isa.us.es/sedl/core/configuration}ExperimentalProcedure"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="tasks" type="{http://isa.us.es/sedl/core/configuration}ExperimentalTask" maxOccurs="unbounded"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TaskBasedExperimentalProcedure", propOrder = {
    "tasks"
})
public class TaskBasedExperimentalProcedure
    extends ExperimentalProcedure
    implements Cloneable, CopyTo, Equals, HashCode
{

    @XmlElement(required = true)
    protected List<ExperimentalTask> tasks;

    /**
     * Gets the value of the tasks property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the tasks property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTasks().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExperimentalTask }
     * 
     * 
     */
    public List<ExperimentalTask> getTasks() {
        if (tasks == null) {
            tasks = new ArrayList<ExperimentalTask>();
        }
        return this.tasks;
    }

    public boolean equals(ObjectLocator thisLocator, ObjectLocator thatLocator, Object object, EqualsStrategy strategy) {
        if (!(object instanceof TaskBasedExperimentalProcedure)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (!super.equals(thisLocator, thatLocator, object, strategy)) {
            return false;
        }
        final TaskBasedExperimentalProcedure that = ((TaskBasedExperimentalProcedure) object);
        {
            List<ExperimentalTask> lhsTasks;
            lhsTasks = (((this.tasks!= null)&&(!this.tasks.isEmpty()))?this.getTasks():null);
            List<ExperimentalTask> rhsTasks;
            rhsTasks = (((that.tasks!= null)&&(!that.tasks.isEmpty()))?that.getTasks():null);
            if (!strategy.equals(LocatorUtils.property(thisLocator, "tasks", lhsTasks), LocatorUtils.property(thatLocator, "tasks", rhsTasks), lhsTasks, rhsTasks)) {
                return false;
            }
        }
        return true;
    }

    public boolean equals(Object object) {
        final EqualsStrategy strategy = JAXBEqualsStrategy.INSTANCE;
        return equals(null, null, object, strategy);
    }

    public int hashCode(ObjectLocator locator, HashCodeStrategy strategy) {
        int currentHashCode = super.hashCode(locator, strategy);
        {
            List<ExperimentalTask> theTasks;
            theTasks = (((this.tasks!= null)&&(!this.tasks.isEmpty()))?this.getTasks():null);
            currentHashCode = strategy.hashCode(LocatorUtils.property(locator, "tasks", theTasks), currentHashCode, theTasks);
        }
        return currentHashCode;
    }

    public int hashCode() {
        final HashCodeStrategy strategy = JAXBHashCodeStrategy.INSTANCE;
        return this.hashCode(null, strategy);
    }

    public Object clone() {
        return copyTo(createNewInstance());
    }

    public Object copyTo(Object target) {
        final CopyStrategy strategy = JAXBCopyStrategy.INSTANCE;
        return copyTo(null, target, strategy);
    }

    public Object copyTo(ObjectLocator locator, Object target, CopyStrategy strategy) {
        final Object draftCopy = ((target == null)?createNewInstance():target);
        super.copyTo(locator, draftCopy, strategy);
        if (draftCopy instanceof TaskBasedExperimentalProcedure) {
            final TaskBasedExperimentalProcedure copy = ((TaskBasedExperimentalProcedure) draftCopy);
            if ((this.tasks!= null)&&(!this.tasks.isEmpty())) {
                List<ExperimentalTask> sourceTasks;
                sourceTasks = (((this.tasks!= null)&&(!this.tasks.isEmpty()))?this.getTasks():null);
                @SuppressWarnings("unchecked")
                List<ExperimentalTask> copyTasks = ((List<ExperimentalTask> ) strategy.copy(LocatorUtils.property(locator, "tasks", sourceTasks), sourceTasks));
                copy.tasks = null;
                if (copyTasks!= null) {
                    List<ExperimentalTask> uniqueTasksl = copy.getTasks();
                    uniqueTasksl.addAll(copyTasks);
                }
            } else {
                copy.tasks = null;
            }
        }
        return draftCopy;
    }

    public Object createNewInstance() {
        return new TaskBasedExperimentalProcedure();
    }

}
