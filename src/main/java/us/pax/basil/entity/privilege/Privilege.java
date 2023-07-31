package us.pax.basil.entity.privilege;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import java.util.*;

@Data
@EqualsAndHashCode(callSuper=false)
@Accessors(chain=true)
@AllArgsConstructor
@ApiModel(value="Privilege Name", description="")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Privilege {
    private Integer id;
    private String label;
    private Privilege[] children;

    public Privilege(int id, String label, Privilege[] children) {
        this.id = id;
        this.label = label;
        this.children = children;
    }


    public void addChild(Privilege child) {
        if (children == null) {
            children = new Privilege[1];
            children[0] = child;
        } else {
            Privilege[] newChildren = new Privilege[children.length + 1];
            System.arraycopy(children, 0, newChildren, 0, children.length);
            newChildren[children.length] = child;
            children = newChildren;
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);
        map.put("label", label);
        map.put("children", children);
        return map;
    }

    public Privilege findPrivilege(int parentId) {
        if (this.id == parentId) {
            return this;
        }
        if (children == null) {
            return null;
        }
        for (Privilege child : children) {
            Privilege privilege = child.findPrivilege(parentId);
            if (privilege != null) {
                return privilege;
            }
        }
        return null;
    }
}
