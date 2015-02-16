package in.istore.bitblue.app.pojo;

public class GridItems {
    private int ivgridItemImage;
    private String tvgridItemTitle;
    private int llgridLayoutcolor;
    public GridItems() {
    }

    public GridItems(int ivgridItemImage, String tvgridItemTitle, int llgridLayoutcolor) {
        this.ivgridItemImage = ivgridItemImage;
        this.tvgridItemTitle = tvgridItemTitle;
        this.llgridLayoutcolor = llgridLayoutcolor;
    }

    public int getIvgridItemImage() {
        return ivgridItemImage;
    }

    public void setIvgridItemImage(int ivgridItemImage) {
        this.ivgridItemImage = ivgridItemImage;
    }

    public String getTvgridItemTitle() {
        return tvgridItemTitle;
    }

    public void setTvgridItemTitle(String tvgridItemTitle) {
        this.tvgridItemTitle = tvgridItemTitle;
    }

    public int getLlgridLayout() {
        return llgridLayoutcolor;
    }

    public void setLlgridLayout(int llgridLayoutcolor) {
        this.llgridLayoutcolor = llgridLayoutcolor;
    }
}
