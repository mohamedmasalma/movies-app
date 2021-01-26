package ocm.sharki.tv.model;

public class TabbarModel {
    public int icon;
    public int id;
    public String title;

    public TabbarModel(String title, int icon, int id) {
        this.title = title;
        this.icon = icon;
        this.id = id;
    }
}
