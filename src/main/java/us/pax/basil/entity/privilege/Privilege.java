package us.pax.basil.entity.privilege;

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
public class Privilege {
    private Integer id;
    private Integer parentId;
    private String name;
    private Privilege[] children;

    public Privilege(int id, int parent_id, String name, Privilege[] children) {
        this.id = id;
        this.parentId = parent_id;
        this.name = name;
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
        map.put("parent_id", parentId);
        map.put("name", name);
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
