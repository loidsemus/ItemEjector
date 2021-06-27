package me.loidsemus.itemejector.database;

public abstract class DataSource {

    public abstract void savePlayer(DataPlayer player);

    public abstract DataPlayer loadPlayer(String uuid);

}
