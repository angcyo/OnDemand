package com.angcyo.ondemand.control;

import com.angcyo.ondemand.OdApplication;
import com.angcyo.ondemand.components.RConstant;
import com.angcyo.ondemand.model.DeliveryserviceBean;
import com.angcyo.ondemand.model.TableCompany;
import com.angcyo.ondemand.model.TableCustomer;
import com.angcyo.ondemand.model.TableDeliveryservice;
import com.angcyo.ondemand.model.TableMember;
import com.angcyo.ondemand.model.TablePlatform;
import com.angcyo.ondemand.model.TableSellerIndex;
import com.angcyo.ondemand.model.TableTradingArea;
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

    public static List<TablePlatform> platforms = new ArrayList();//平台 饿了么/美团/等
    public static List<TableDeliveryservice> deliveryservices = new ArrayList();//所有订单
    public static List<TableDeliveryservice> deliveryservicesToday = new ArrayList();//今天的订单
    public static List<TableMember> members = new ArrayList();//配送员
    public static List<TableCompany> companys = new ArrayList();//配送公司,xx物流
    public static List<TableCustomer> customers = new ArrayList();//消费者信息
    public static List<TableSellerIndex> sellerIndexes = new ArrayList();//服务商家

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

    /**
     * 根据平台名称, 获取平台sid
     */
    public static int getSidWithPlatform(String platform) {
        if (Util.isEmpty(platform)) {
            return -1;
        }
        for (TablePlatform plat : platforms) {
            if (plat.getCaption().equalsIgnoreCase(platform)) {
                return plat.getSid();
            }
        }
        return -1;
    }


    /**
     * 根据订单号和平台id, 获取订单id
     */
    public static int getSidWithPOddnum(String oddnum, int platformSid) {
        if (Util.isEmpty(oddnum) || platformSid < 1) {
            return -1;
        }
        for (TableDeliveryservice deliveryservice : deliveryservicesToday) {
            if (deliveryservice.getSeller_order_identifier().equalsIgnoreCase(oddnum) && deliveryservice.getSid_ec() == platformSid) {
                return deliveryservice.getSid();
            }
        }
        return -1;
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
                data.setSeller_order_identifier(rs.getString(2).trim());
                data.setSid_ec(rs.getInt(3));
                data.setSid_seller(rs.getInt(4));
                data.setSid_customer(rs.getInt(5));
                data.setSid_member(rs.getInt(6));
                data.setDt_locked(rs.getString(7));
                data.setDt_start(rs.getString(8));
                data.setDt_end(rs.getString(9));
                data.setStatus(rs.getInt(10));
                data.setComment(rs.getString(11));
                data.setDt_create(rs.getString(12));
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
                if (isToady(de.getDt_create()) && de.getStatus() != 9
                        && de.getSid_seller() == OdApplication.userInfo.sellerIndex.getSid()) {//今天的订单,并且订单状态是未完成,并且订单是当前登录的服务商家
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
        String phone = "";
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
                data.setId_tradingarea(rs.getInt(8));//v2.0 新增
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

    // seller_index 服务商家
    public static List<TableSellerIndex> getAllSellerIndexes() {
        sellerIndexes = new ArrayList();
        Connection connection;
        TableSellerIndex data;
        try {
            connection = getDb();
            Statement statement = connection.createStatement();
            String queryString = "SELECT * from seller_index";
            ResultSet rs = statement.executeQuery(queryString);
            while (rs.next()) {
                data = new TableSellerIndex();
                data.setSid(rs.getInt(1));
                data.setCompany(rs.getString(2));
                data.setAddress(rs.getString(3));
                data.setContacts(rs.getString(4));
                data.setPhone(rs.getString(5));
                sellerIndexes.add(data);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return sellerIndexes;
    }

    // seller_index 服务商家
    public static String getCaptionWithSellerSid(int sid) {
        String caption = "";
        for (TableSellerIndex seller : sellerIndexes) {
            if (seller.getSid() == sid) {
                caption = seller.getCompany();
                return caption;
            }
        }
        return caption;
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
                try {
                    data.setSid(rs.getInt(1));
                    data.setNickname(rs.getString(2));
                    data.setName(rs.getString(3));
                    data.setPid(rs.getInt(4));
                    data.setAddress(rs.getString(5));
                    data.setPhone(rs.getString(6));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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
                Util.getDateAndTime(), Util.getDateAndTime(), Util.getDateAndTime(), status, Util.getDeviceModelName() + " " + RConstant.VERSION);
        statement.executeUpdate(queryString);
        connection.close();
    }

    /**
     * 更新订单状态
     * 订单状态（0待命 1锁单 2派送中 3派送丢失 4客户拒收 9客户已收）
     */
    public static void updateOddnumState(String seller_order_identifier, int status) throws SQLException, ClassNotFoundException {
        //UPDATE order_deliveryservice set status = 9  WHERE seller_order_identifier = '222'
        Connection connection;
        connection = getDb();
        Statement statement = connection.createStatement();
        String queryString = String.format("UPDATE order_deliveryservice set status = %d , dt_end = '%s' " +
                        "WHERE seller_order_identifier = '%s' and dt_create>='%s'",
                status, Util.getDateAndTime(), seller_order_identifier, Util.getDate());
        statement.executeUpdate(queryString);
        connection.close();
    }

    /**
     * 更新订单数据
     * sid_ec			int	4	10	0			电商平台ID
     * sid_seller			int	4	10	0		((0))	所属商户ID
     * sid_customer			int	4	10	0		((0))	消费者ID
     * sid_member			int	4	10	0		((0))	配送员ID
     * dt_start			datetime	8	23	3	√		配送开始时间（配送员取到派送物件）
     *
     * @param status the status
     * @throws SQLException           the sql exception
     * @throws ClassNotFoundException the class not found exception
     */
    public static void updateOddnum(int sid_member, int status, int sid) throws SQLException, ClassNotFoundException {
        //UPDATE order_deliveryservice set status = 9  WHERE seller_order_identifier = '222'
        Connection connection;
        connection = getDb();
        Statement statement = connection.createStatement();
//        String queryString = String.format("UPDATE order_deliveryservice set status = %d , dt_end = '%s' WHERE seller_order_identifier = '%s'",
//                status, Util.getDateAndTime(), seller_order_identifier);


//        Connection connection;
//        connection = getDb();
//        Statement statement = connection.createStatement();
        String queryString = String.format("UPDATE order_deliveryservice set sid_member = %d,dt_locked = '%s', dt_start = '%s', status = %d, comment = '%s' WHERE sid = %d",
                sid_member, Util.getDateAndTime(), Util.getDateAndTime(), status, Util.getDeviceModelName() + " " + RConstant.VERSION, sid);
        statement.executeUpdate(queryString);
        connection.close();

//        statement.executeUpdate(queryString);
//        connection.close();
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


    /**
     * 2016-1-18 v2.0 修改
     */

    /**
     * 添加配送员
     *
     * @param member the member
     */
    public static int addMember(TableMember member) throws SQLException, ClassNotFoundException {
        Connection connection;
        TableMember data;
        connection = getDb();
        Statement statement = connection.createStatement();
        //INSERT INTO ds_member VALUES ('nickname','name','phone',-1,'2015-01-13 11:11','psw',1)
        String queryString = String.format("INSERT INTO ds_member VALUES ('%s','%s','%s',%d,'%s','%s',%d)",
                member.getName_login(), member.getName_real(), member.getPhone(),
                member.getId_company(), Util.getDateAndTime(), member.getPsw(), member.getId_tradingarea());
        int row = statement.executeUpdate(queryString);//返回受影响的行数
        connection.close();
        return row;
    }


    /**
     * 判断用户是否存在
     *
     * @param phone 用户手机号码
     * @return the boolean
     */
    public static boolean isMemberExist(String phone) {
        boolean result = false;
        Connection connection;
        try {
            connection = getDb();
            Statement statement = connection.createStatement();
            String queryString = String.format("SELECT * FROM ds_member WHERE phone = '%s'",
                    phone);
            ResultSet rs = statement.executeQuery(queryString);
            result = rs.next();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 判断订单状态是否已完成
     */
    public static boolean isOrderFinish(String seller_order_identifier) {
        boolean result = false;
        Connection connection;
        try {
            connection = getDb();
            Statement statement = connection.createStatement();
            String rawSql = "SELECT * from order_deliveryservice WHERE seller_order_identifier='%s' and status=9  and dt_create>='%s'";
            String queryString = String.format(rawSql,
                    seller_order_identifier, Util.getDate());
            ResultSet rs = statement.executeQuery(queryString);
            result = rs.next();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * 返回登录成功后的用户信息
     */
    public static TableMember getLoginMember(String phone, String md5Pwd) {
        TableMember member = null;
        Connection connection;
        try {
            connection = getDb();
            Statement statement = connection.createStatement();
            String queryString = String.format("SELECT * FROM ds_member WHERE phone = '%s' AND psw = '%s'",
                    phone, md5Pwd);
            ResultSet rs = statement.executeQuery(queryString);
            while (rs.next()) {
                member = new TableMember();
                member.setSid(rs.getInt(1));
                member.setName_login(rs.getString(2));
                member.setName_real(rs.getString(3));
                member.setPhone(rs.getString(4));
                member.setId_company(rs.getInt(5));
                member.setOnline(rs.getString(6));
                member.setPsw(rs.getString(7));
                member.setId_tradingarea(rs.getInt(8));//v2.0 新增
                break;//返回第一个
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return member;
    }

    /**
     * 获取所有商圈
     */
    public static ArrayList<TableTradingArea> getAllTradingArea() {
        ArrayList<TableTradingArea> tradingAreas = new ArrayList<>();
        Connection connection;
        TableTradingArea data;
        try {
            connection = getDb();
            Statement statement = connection.createStatement();
            String queryString = "SELECT * FROM od_tradingarea";
            ResultSet rs = statement.executeQuery(queryString);
            while (rs.next()) {
                data = new TableTradingArea();
                data.setPid(rs.getInt(1));
                data.setSid(rs.getInt(2));
                data.setDscrp(rs.getString(3));
                data.setSeqc(rs.getInt(4));
                data.setClsid(rs.getInt(5));
                tradingAreas.add(data);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return tradingAreas;
    }

    /**
     * 获取配送员和商圈都相同,切状态为0,今天的订单
     */
    public static ArrayList<DeliveryserviceBean> getAllDeliveryservice2() {
        //select * from dbo.get_tradingarea_order_seller(50) where  status=0 and dt_create>='2015-11-16 12:00'
        ArrayList<DeliveryserviceBean> beans = new ArrayList<>();
        Connection connection;
        DeliveryserviceBean data;
        try {
            connection = getDb();
            Statement statement = connection.createStatement();
            String rawSql = "select * from dbo.get_tradingarea_order_seller(50) where  status=0 and dt_create>='%s'";
            String queryString = String.format(rawSql, Util.getDate());
            ResultSet rs = statement.executeQuery(queryString);
            while (rs.next()) {
                data = new DeliveryserviceBean();
                data.setSid(rs.getInt(1));
                data.setSeller_order_identifier(rs.getString(2));
                data.setStatus(rs.getInt(3));
                data.setComment(rs.getString(4));
                data.setDt_create(rs.getString(5));
                data.setCaption(rs.getString(6));
                data.setAddress(rs.getString(7));
                data.setEc_caption(rs.getString(8));
                data.setName(rs.getString(9));
                data.setPhone(rs.getString(10));
                beans.add(data);
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return beans;
    }
}
