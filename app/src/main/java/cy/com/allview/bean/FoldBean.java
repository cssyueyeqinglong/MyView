package cy.com.allview.bean;

/**
 * Created by Administrator
 * on 2017/4/10.
 * des:
 */

public class FoldBean {
    private String dir;//图片的目录
    private String name;//最底层目录名称
    private int count;//目录下图片数量
    private String firstNamePath;

    public String getFirstNamePath() {
        return firstNamePath;
    }

    public void setFirstNamePath(String firstNamePath) {
        this.firstNamePath = firstNamePath;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
        int lastIndex=dir.lastIndexOf("/");
        name=dir.substring(lastIndex+1);
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
