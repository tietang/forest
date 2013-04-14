package fengfei.exmaple;

import java.sql.ResultSet;
import java.sql.SQLException;

import fengfei.forest.database.dbutils.Transducer;

public class UserTransducer implements Transducer<User> {

	@Override
	public User transform(ResultSet rs) throws SQLException {
		long idUser = rs.getLong("id_user");
		String email = rs.getString("email");
		String userName = rs.getString("username");
		String password = rs.getString("password");

		return new User(idUser, userName, email, password);
	}

}
