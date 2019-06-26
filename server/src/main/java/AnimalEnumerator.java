public enum AnimalEnumerator {
    GATO("/gato"), CACHORRO("/cachorro"), NOT_SET("/");
    private String url;

    AnimalEnumerator(String s){
        this.url=s;
    }


    public String getUrl(){
        return url;
    }
}
