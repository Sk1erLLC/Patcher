package club.sk1er.patcher.database;

import club.sk1er.patcher.hooks.FallbackResourceManagerHook;
import net.minecraft.launchwrapper.Launch;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    public AssetsDatabase() {
        try {
            File minecraftHome = Launch.minecraftHome;
            if (minecraftHome == null) minecraftHome = new File(".");
            dir = new File(minecraftHome, "patcher");
            dir.mkdir();
            connection = DriverManager.getConnection("jdbc:h2:" + dir.getAbsolutePath() + "/asset_cache_from_patcher_mod.h2", "", "");
            connection.prepareStatement("create table if not exists assets (pack varchar(256), name varchar(1024), data BINARY, mcmeta BINARY)").executeUpdate();
            connection.prepareStatement("create index if not exists name on assets (name)").executeUpdate();
            connection.prepareStatement("create index if not exists pack_name on assets (pack,name)").executeUpdate();
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

    private static byte[] read(InputStream stream) throws IOException {
        byte[] bytes = new byte[stream.available()];
        IOUtils.read(stream, bytes);
        return bytes;
    }

    public DatabaseReturn getData(String name) {
        try {
            PreparedStatement statement = connection.prepareStatement("select * from assets where name=?");
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                InputStream data = resultSet.getBinaryStream("data");
                InputStream mcmeta = resultSet.getBinaryStream("mcmeta");
                boolean noMeta = resultSet.wasNull();
                return new DatabaseReturn(read(data), noMeta ? null : read(mcmeta), resultSet.getString("pack"));
            }
            return null;
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
        return null;
    }

    public void clearAll() {
        try {
            connection.prepareStatement("delete from assets").executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void clearPack(String pack) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("delete from assets where pack=?");
            preparedStatement.setString(1, pack);
            preparedStatement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void update(String pack, String name, byte[] data, byte[] mcMeta) {
        try {
            PreparedStatement statement = connection.prepareStatement("merge into assets key(pack,`name`) values (?,?,?,?)");
            statement.setString(1, pack);
            statement.setString(2, name);
            statement.setBinaryStream(3, new ByteArrayInputStream(data));
            statement.setBinaryStream(4, mcMeta == null ? null : new ByteArrayInputStream(mcMeta));

            statement.executeUpdate();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public List<String> getAllNegative() throws IOException {
        File file = new File(dir, "negative_cache.txt");
        if (file.exists())
            return FileUtils.readLines(file);
        return new ArrayList<>();
    }

    public void saveNegative(Set<String> lines) throws IOException {
        FileUtils.writeLines(new File(dir, "negative_cache.txt"), lines);
    }

}
