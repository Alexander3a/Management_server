package de.alex.money_server;

public enum hwids {
    HOME("8BB09A1159BE36A78EA83EF0F85E2E62"),SERVER("ad");


    private String url;

    hwids(String envUrl) {
        this.url = envUrl;
    }

    public String getUrl() {
        return url;
    }
}
