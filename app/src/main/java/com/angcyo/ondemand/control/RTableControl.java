package com.angcyo.ondemand.control;

import com.angcyo.ondemand.components.RConstant;
import com.angcyo.ondemand.model.TableCompany;
import com.angcyo.ondemand.model.TableCustomer;
import com.angcyo.ondemand.model.TableDeliveryservice;
import com.angcyo.ondemand.model.TableMember;
import com.angcyo.ondemand.model.TablePlatform;
import com.angcyo.ondemand.util.Util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by angcyo on 15-09-26-026.
 */
public class RTableControl {

    public static List<TablePlatform> platforms = new ArrayList();
    public static List<TableDeliveryservice> deliveryservices = new ArrayList();
    public static List<TableDeliveryservice> deliveryservicesToday = new ArrayList();
    public static List<TableMember> members = new ArrayList();
    public static List<TableCompany> companys = new ArrayList();
    public static List<TableCustomer> customers = new ArrayList();

    private static ResultSet getResult(Connection connection, String queryString) throws SQLException {
        Statement statement = connection.createStatement();
        return statement.executeQuery(queryString);
    }

    //ec_platform电子商务平台信息
    public static List<TablePlatform> getAllPlatform() {
        platforms = new ArrayList();
        Connection connection;
        TablePlatform data;
        try {
            connection = getDb();
            String queryString = "SELECT * from ec_platform";
            ResultSet rs = getResult(connection, queryString);
            while (rs.next()) {
                data = new TablePlatform();
                data.setSid(rs.getInt(1));
                data.setCaption(rs.getString(2));
                platforms.add(data);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return platforms;
    }

    // order_deliveryservice 商户订单配送情况
    public static List<TableDeliveryservice> getAllDeliveryservice() {
        deliveryservices = new ArrayList();
        Connection connection;
        TableDeliveryservice data;
        try {
            connection = getDb();
            Statement statement = connection.createStatement();
            String queryString = "SELECT * from order_deliveryservice";
            ResultSet rs = statement.executeQuery(queryString);
            while (rs.next()) {
                data = new TableDeliveryservice();
                data.setSid(rs.getInt(1));
                data.setSeller_order_identifier(rs.getString(2));
                data.setSid_ec(rs.getInt(3));
                data.setSid_seller(rs.getInt(4));
                data.setSid_customer(rs.getInt(5));
                data.setSid_member(rs.getInt(6));
                data.setDt_locked(rs.getString(7));
                data.setDt_start(rs.getString(8));
                data.setDt_end(rs.getString(9));
                data.setStatus(rs.getInt(10));
                data.setComment(rs.getString(11));
                deliveryservices.add(data);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return deliveryservices;
    }

    /**
     * 获取今天的所有订单
     */
    public static List<String> getDeliveryservicesToday() {
        deliveryservicesToday = new ArrayList<>();
        List<String> des = new ArrayList<>();
        for (TableDeliveryservice de : RTableControl.deliveryservices) {
            try {
                if (isToady(de.getDt_locked())) {
                    des.add(de.getSeller_order_identifier());
                    deliveryservicesToday.add(de);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return des;
    }

    /**
     * 根据订单号,返回消费者手机号码
     */
    public static String getPhoneWithOddnum(String oddnum) {
        String phone = "123";
        for (TableDeliveryservice de : RTableControl.deliveryservicesToday) {
            if (de.getSeller_order_identifier().equalsIgnoreCase(oddnum)) {
                for (TableCustomer mer : customers) {
                    if (mer.getSid() == de.getSid_customer()) {//消费者id
                        return mer.getPhone();
                    }
                }
            }
        }
        return phone;
    }

    /**
     * 判断当前日期是否是今天 2015-10-13 格式
     */
    public static boolean isToady(String date) {
        return date.startsWith(Util.getDate());
    }

    // ds_member 配送员信息（移动终端）
    public static List<TableMember> getAllMember() {
        members = new ArrayList();
        Connection connection;
        TableMember data;
        try {
            connection = getDb();
            Statement statement = connection.createStatement();
            String queryString = "SELECT * from ds_member";
            ResultSet rs = statement.executeQuery(queryString);
            while (rs.next()) {
                data = new TableMember();
                data.setSid(rs.getInt(1));
                data.setName_login(rs.getString(2));
                data.setName_real(rs.getString(3));
                data.setPhone(rs.getString(4));
                data.setId_company(rs.getInt(5));
                data.setOnline(rs.getString(6));
                data.setPsw(rs.getString(7));
                members.add(data);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return members;
    }

    // ds_company 配送公司信息
    public static List<TableCompany> getAllCompany() {
        companys = new ArrayList();
        Connection connection;
        TableCompany data;
        try {
            connection = getDb();
            Statement statement = connection.createStatement();
            String queryString = "SELECT * from ds_company";
            ResultSet rs = statement.executeQuery(queryString);
            while (rs.next()) {
                data = new TableCompany();
                data.setSid(rs.getInt(1));
                data.setCaption(rs.getString(2));
                data.setAddress(rs.getString(3));
                data.setContacts(rs.getString(4));
                data.setPhone(rs.getString(5));
                companys.add(data);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return companys;
    }

    // customer_index	消费者基本信息
    public static List<TableCustomer> getAllCustomer() {
        customers = new ArrayList();
        Connection connection;
        TableCustomer data;
        try {
            connection = getDb();
            Statement statement = connection.createStatement();
            String queryString = "SELECT * from customer_index";
            ResultSet rs = statement.executeQuery(queryString);
            while (rs.next()) {
                data = new TableCustomer();
                data.setSid(rs.getInt(1));
                data.setNickname(rs.getString(2));
                data.setName(rs.getString(3));
                data.setPid(rs.getInt(4));
                data.setAddress(rs.getString(5));
                data.setPhone(rs.getString(6));
                customers.add(data);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return customers;
    }

    public static void updateOnline(int sid) {//更新在线时间
        //UPDATE ds_member SET online = '124125125' WHERE sid = '10'
        Connection connection;
        try {
            connection = getDb();
            Statement statement = connection.createStatement();
            String queryString = String.format("UPDATE ds_member SET online = '%s' WHERE sid = %d", Util.getDateAndTime(), sid);
            statement.executeUpdate(queryString);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    //            1、手工录入订单号：（对应传给 seller_order_identifier）
    //            2、从ec_platform获取SID：（对应传给 sid_ec）
    //            3、从seller_index获取SID：（对应传给 sid_seller）
    //            4、从customer_index获取SID：（对应传给 sid_customer）
    //            5、从ds_member获取SID：（对应传给 sid_member）

//    sid	√	√	int	4	10	0			单据ID
//    seller_order_identifier			varchar	20	20	0			商户订单号
//    sid_ec			int	4	10	0			电商平台ID
//    sid_seller			int	4	10	0		((0))	所属商户ID
//    sid_customer			int	4	10	0		((0))	消费者ID
//    sid_member			int	4	10	0		((0))	配送员ID
//    dt_locked			datetime	8	23	3	√		锁单时间（配送员终端接受送任务）
//    dt_start			datetime	8	23	3	√		配送开始时间（配送员取到派送物件）
//    dt_end			datetime	8	23	3	√		配送完成时间（配送员派送到达目的地完成文件交接）
//    status			tinyint	1	3	0		((0))	订单状态（0待命 1锁单 2派送中 3派送丢失 4客户拒收 9客户已收）
//    comment			varchar	100	100	0	√		备注

    public static void executeOddnum(String seller_order_identifier, int sid_ec, int sid_seller, int sid_customer, int sid_member, int status) throws SQLException, ClassNotFoundException {
        //INSERT INTO order_deliveryservice VALUES ('71238', 3, 0, 0, 17, '2015-08-02 11:00', '2015-09-02 12:11', '2015-09-28 14:22', 9, 'comment')
        Connection connection;
        connection = getDb();
        Statement statement = connection.createStatement();
        String queryString = String.format("INSERT INTO order_deliveryservice VALUES ('%s', %d, %d, %d, %d, '%s', '%s', '%s', %d, '%s')",
                seller_order_identifier, sid_ec, sid_seller, sid_customer, sid_member,
                Util.getDateAndTime(), Util.getDateAndTime(), Util.getDateAndTime(), status, Util.getDeviceName() + " " + RConstant.VERSION);
        statement.executeUpdate(queryString);
        connection.close();
    }

    public static void updateOddnumState(String seller_order_identifier, int status) throws SQLException, ClassNotFoundException {
        //UPDATE order_deliveryservice set status = 9  WHERE seller_order_identifier = '222'
        Connection connection;
        connection = getDb();
        Statement statement = connection.createStatement();
        String queryString = String.format("UPDATE order_deliveryservice set status = %d , dt_end = '%s' WHERE seller_order_identifier = '%s'",
                status, Util.getDateAndTime(), seller_order_identifier);
        statement.executeUpdate(queryString);
        connection.close();
    }

    public static int callUpdate(int sid_member, double lat, double log) throws SQLException, ClassNotFoundException {
        int result;
        Connection connection;
        connection = getDb();
        CallableStatement statement = connection.prepareCall("{call Notify_Online(?,?,?,?,?)}");
        // 设置IN参数
        statement.setInt(1, sid_member);//配送员id
        statement.setDouble(2, lat);//经度
        statement.setDouble(3, log);//维度
        statement.setInt(4, 1);//定位方法：（可选，0 GPS，1 LBS(基站））
        // 注册OUT参数
        statement.registerOutParameter(5, Types.INTEGER);
        // 执行存储过程
        statement.execute();
        result = statement.getInt(5);
        connection.close();
        return result;
    }

    public static Connection getDb() throws SQLException, ClassNotFoundException {
        String url = "jdbc:jtds:sqlserver://223.244.227.14:21006/OnDemand";//;charset=UTF-8
        return connectDatabase(url, "xzsoft1", "xzsoft1");
    }

    public static Connection connectDatabase(String db_connect_string, String db_userid, String db_password) throws ClassNotFoundException, SQLException {
        //jtds方式
        Class.forName("net.sourceforge.jtds.jdbc.Driver");//net.sourceforge.jtds.jdbc.Driver
        Connection conn = DriverManager.getConnection(db_connect_string, db_userid, db_password);
        return conn;
    }

}
