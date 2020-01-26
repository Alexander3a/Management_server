package de.alex.money_server;

public enum hwids {
    HOME("0A863245054456E0F816576A7882CE13"),SERVER("ad");


    private String url;

    hwids(String envUrl) {
        this.url = envUrl;
    }

    public String getUrl() {
        return url;
    }
}
