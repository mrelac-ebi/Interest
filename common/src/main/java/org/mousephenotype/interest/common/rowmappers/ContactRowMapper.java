package org.mousephenotype.interest.common.rowmappers;

import org.mousephenotype.interest.common.entities.Contact;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created by mrelac on 12/05/2017.
 */
public class ContactRowMapper implements RowMapper<Contact> {

    /**
     * Implementations must implement this method to map each row of data
     * in the ResultSet. This method should not call {@code next()} on
     * the ResultSet; it is only supposed to map values of the current row.
     *
     * @param rs     the ResultSet to map (pre-initialized for the current row)
     * @param rowNum the number of the current row
     * @return the result object for the current row
     * @throws SQLException if a SQLException is encountered getting
     *                      column values (that is, there's no need to catch SQLException)
     */
    @Override
    public Contact mapRow(ResultSet rs, int rowNum) throws SQLException {
        Contact contact = new Contact();

        contact.setPk(rs.getInt("pk"));
        contact.setAddress((rs.getString("address")));
        int active = rs.getInt("active");
        contact.setActive(active > 0 ? true : false);
        contact.setUpdatedAt(new Date(rs.getTimestamp("updated_at").getTime()));

        return contact;
    }
}