package club.sk1er.patcher.database;

import club.sk1er.patcher.hooks.FallbackResourceManagerHook;
import net.minecraft.launchwrapper.Launch;
import org.apache.commons.io.FileUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class AssetsDatabase {

    private Connection connection;
    private File dir;
    private boolean connected = false;
    private boolean fresh = false;

    public AssetsDatabase() {
        try {
            File minecraftHome = Launch.minecraftHome;
            if (minecraftHome == null) minecraftHome = new File(".");
            dir = new File(minecraftHome, "patcher");
            fresh = !dir.exists();
            dir.mkdir();
            connection = DriverManager.getConnection("jdbc:h2:" + dir.getAbsolutePath() + "/assets_cache.h2", "", "");
            connection.prepareStatement("create table if not exists assets (pack varchar(256), name varchar(1024), data BINARY, mcmeta BINARY, main_size INT, meta_size INT)").executeUpdate();
            connection.prepareStatement("create unique index if not exists name on assets (name)").executeUpdate();
            connected = true;
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        if (connection != null)
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    saveNegative(FallbackResourceManagerHook.negativeResourceCache);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    connection.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }));
    }

    public static byte[] read(InputStream inputStream, int size) throws IOException {
        byte[] data = new byte[size];
        inputStream.read(data);
        return data;
    }


    public DatabaseReturn getData(String name) {
        if (!connected) return null;
        try {
            PreparedStatement statement = connection.prepareStatement("select * from assets where name=? limit 1");
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                InputStream data = resultSet.getBinaryStream("data");
                InputStream mcmeta = resultSet.getBinaryStream("mcmeta");
                boolean noMeta = resultSet.wasNull();
                int mainSize = resultSet.getInt("main_size");
                int metaSize = noMeta ? 0 : resultSet.getInt("meta_size");
                DatabaseReturn pack = new DatabaseReturn(read(data, mainSize), noMeta ? null : read(mcmeta, metaSize), resultSet.getString("pack"));
                resultSet.close();
                return pack;
            }
            resultSet.close();
            return null;
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void clearAll() {
        if (!connected) return;
        try {
            connection.prepareStatement("truncate table assets").executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }


    public void update(String pack, String name, byte[] data, byte[] mcMeta) {
        if (!connected) return;
        try {
            PreparedStatement statement = connection.prepareStatement("merge into assets key(`name`) values (?,?,?,?,?,?)");
            statement.setString(1, pack);
            statement.setString(2, name);
            statement.setBinaryStream(3, new ByteArrayInputStream(data));
            statement.setBinaryStream(4, mcMeta == null ? null : new ByteArrayInputStream(mcMeta));
            statement.setInt(5, data.length);
            statement.setInt(6, mcMeta == null ? 0 : mcMeta.length);
            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public List<String> getAllNegative() throws IOException {
        File file = new File(dir, "negative_cache.txt");
        if (file.exists())
            return FileUtils.readLines(file, Charset.defaultCharset());
        return new ArrayList<>();
    }

    public void saveNegative(Set<String> lines) throws IOException {
        FileUtils.writeLines(new File(dir, "negative_cache.txt"), lines);
    }

    public boolean isNew() {
        return fresh;
    }
}
