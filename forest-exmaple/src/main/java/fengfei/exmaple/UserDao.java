package fengfei.exmaple;

import java.sql.SQLException;

import fengfei.forest.database.dbutils.ForestGrower;
import fengfei.forest.database.dbutils.StringTransducer;

public class UserDao {

	public static int save(ForestGrower grower, String suffix, User user)
			throws SQLException {

		String insert = String
				.format("INSERT INTO user%s( email, username, password) VALUES ( ?,?,?)",
						suffix);
		int updated = grower.update(insert, user.getEmail(),
				user.getUserName(), user.getPassword());

		return updated;
	}

	public static int updatePassword(ForestGrower grower, String suffix,
			User user) throws SQLException {
		String update = "update user" + suffix
				+ " set password=?   where username=? or email=? or id_user=?";
		int updated = grower.update(update, user.getPassword(),
				user.getUserName(), user.getEmail(), user.getIdUser());
		return updated;
	}

	public static boolean isExists(ForestGrower grower, String suffix,
			String usernameOrEmail) throws SQLException {
		String sql = "select id_user from user" + suffix
				+ " where  username=? or email=?  ";
		String id = grower.selectOne(sql, new StringTransducer(),
				usernameOrEmail, usernameOrEmail);
		return id != null && !"".equals(id);
	}

	public static User get(ForestGrower grower, String suffix, int idUser)
			throws SQLException {
		String sql = "SELECT id_user, email, username FROM user" + suffix
				+ " where id_user=?";
		User user = grower.selectOne(sql, new UserTransducer(), idUser);
		return user;
	}

	public static User get(ForestGrower grower, String suffix, String email,
			String pwd) throws SQLException {
		String sql = "SELECT id_user, email, username FROM user" + suffix
				+ " where email=? and password=?";
		User user = grower.selectOne(sql, new UserTransducer(), email, pwd);
		return user;
	}

}
