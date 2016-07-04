package naga.core.spi.platform.java;

import com.zaxxer.hikari.HikariDataSource;
import naga.core.queryservice.QueryArgument;
import naga.core.queryservice.QueryResultSet;
import naga.core.queryservice.QueryService;
import naga.core.queryservice.impl.ConnectionDetails;
import naga.core.updateservice.UpdateArgument;
import naga.core.updateservice.UpdateResult;
import naga.core.updateservice.UpdateService;
import naga.core.util.Arrays;
import naga.core.util.async.Future;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Bruno Salmon
 */
class JdbcConnectedService implements QueryService, UpdateService {

    private final DataSource jdbcDataSource;

    public JdbcConnectedService(ConnectionDetails connectionDetails) {
        HikariDataSource hikariDS = new HikariDataSource();
        hikariDS.setDriverClassName(connectionDetails.getDBMS().getJdbcDriverClass());
        hikariDS.setJdbcUrl(connectionDetails.getUrl());
        hikariDS.setUsername(connectionDetails.getUsername());
        hikariDS.setPassword(connectionDetails.getPassword());
        jdbcDataSource = hikariDS;
    }

    @Override
    public Future<QueryResultSet> read(QueryArgument arg) {
        Future<QueryResultSet> future = Future.future();

        String sql = arg.getQueryString();
        try (
                Connection connection = getConnection();
                Statement statement = getStatement(sql, arg.getParameters(), connection);
                ResultSet resultSet = executeStatementQuery(sql, statement)
        ) {
            // Reading column names
            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            String[] columnNames = new String[columnCount];
            for (int columnIndex = 0; columnIndex < columnCount; columnIndex++)
                columnNames[columnIndex] = metaData.getColumnName(columnIndex + 1); // JDBC index starts with 1 (not 0)
            // Reading data through iterating the result set into a temporary growing list of rows (as we don't know yet the rows count)
            List<Object[]> rows = new ArrayList<>(100); // Default capacity = 100 (as default limit is 100 is Naga).
            while (resultSet.next()) {
                Object[] columns = new Object[columnCount];
                for (int columnIndex = 0; columnIndex < columnCount; columnIndex++)
                    columns[columnIndex] = resultSet.getObject(columnIndex + 1); // JDBC index starts with 1 (not 0)
                rows.add(columns);
            }
            // Now we have the rows count
            int rowCount = rows.size();
            // Moving the data into the final inline array values representation
            Object[] inlineValues = new Object[rowCount * columnCount];
            for (int rowIndex = 0; rowIndex < rowCount; rowIndex++)
                for (int columnIndex = 0; columnIndex < columnCount; columnIndex++)
                    inlineValues[rowIndex + columnIndex * rowCount] = rows.get(rowIndex)[columnIndex];
            // Returning the SQL result
            future.complete(new QueryResultSet(inlineValues, columnNames));
        } catch (Throwable throwable) {
            future.fail(throwable);
        }

        return future;
    }

    @Override
    public Future<UpdateResult> update(UpdateArgument arg) {
        Future<UpdateResult> future = Future.future();

        String sql = arg.getUpdateString();
        try (
                Connection connection = getConnection();
                Statement statement = getStatement(sql, arg.getParameters(), connection)
        ) {
            boolean returnGeneratedKeys = arg.returnGeneratedKeys();
            int rowCount = executeStatementUpdate(sql, statement, returnGeneratedKeys);
            Object[] generatedKeys = null;
            if (returnGeneratedKeys) {
                ResultSet rs = statement.getGeneratedKeys();
                List keysList = new ArrayList();
                while (rs.next())
                    keysList.add(rs.getObject(0));
                generatedKeys = keysList.toArray();
            }
            future.complete(new UpdateResult(rowCount, generatedKeys));
        } catch (Throwable throwable) {
            future.fail(throwable);
        }

        return future;
    }

    private Connection getConnection() throws SQLException {
        return jdbcDataSource.getConnection();
    }

    private Statement getStatement(String sql, Object[] parameters, Connection connection) throws SQLException {
        if (Arrays.isEmpty(parameters))
            return connection.createStatement();
        //SqlPrepared p = e.getSqlPrepared();
        //PreparedStatement ps = connection.prepareStatement(p.getQueryString(), p.getAutoGeneratedKeyColumnNames());
        PreparedStatement ps = connection.prepareStatement(sql);
        for (int i = 0; i < parameters.length; i++) {
            Object parameter = parameters[i];
            /*if (parameter instanceof ParameterJoinValue)
                parameter = ((ParameterJoinValue) parameter).getRowId();
            while (parameter instanceof ID)
                parameter = ((ID) parameter).getObjId();*/
            if (parameter instanceof Date)
                ps.setDate(i + 1, (Date) parameter);
            else if (parameter instanceof Timestamp)
                ps.setTimestamp(i + 1, (Timestamp) parameter);
            else if (parameter instanceof java.util.Date) { // Postgres doesn't accept setObject() with dates but requires explicit setDate()
                java.util.Date date = (java.util.Date) parameter;
                if (date.getHours() == 0 && date.getMinutes() == 0 && date.getSeconds() == 0)
                    ps.setDate(i + 1, new Date(date.getTime()));
                else
                    ps.setTimestamp(i + 1, new Timestamp(date.getTime()));
            } else
                //try {
                if (parameter != null)
                    ps.setObject(i + 1, parameter);
                else
                    ps.setNull(i + 1, Types.INTEGER);  // Postgres needs the type in some case (ex: ? is null). Putting Integer to fit keys but what if it's not the case ...?
                /*} catch (SQLException e1) {
                    e1.printStackTrace();
                    throw e1;
                }*/
        }
        return ps;
    }

    private ResultSet executeStatementQuery(String sql, Statement statement) throws SQLException {
        if (statement instanceof PreparedStatement)
            return ((PreparedStatement) statement).executeQuery();
        return statement.executeQuery(sql);
    }

    private int executeStatementUpdate(String sql, Statement statement, boolean returnGeneratedKeys) throws SQLException {
        if (statement instanceof PreparedStatement)
            return ((PreparedStatement) statement).executeUpdate();
        return statement.executeUpdate(sql, returnGeneratedKeys ? Statement.RETURN_GENERATED_KEYS : Statement.NO_GENERATED_KEYS);
    }

}
