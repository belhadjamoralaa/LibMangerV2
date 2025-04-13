package librarymanagement;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MemberDAOImpl implements MemberDAO {
    private Connection connection;

    public MemberDAOImpl() {
        this.connection = DatabaseConnection.getInstance().getConnection();
    }

    @Override
    public void create(Member member) {
        String query = "INSERT INTO members (name, discount_type, discount_value) VALUES (?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            String discountType = "";
            double discountValue = 0.0;
            
            if (member.getPriceStrategy() instanceof CodeDiscountStrategy) {
                discountType = "code";
                CodeDiscountStrategy strategy = (CodeDiscountStrategy) member.getPriceStrategy();
                discountValue = strategy.getPercentageOff();
            } else if (member.getPriceStrategy() instanceof MemberDiscountStrategy) {
                discountType = "fixed";
                MemberDiscountStrategy strategy = (MemberDiscountStrategy) member.getPriceStrategy();
                discountValue = strategy.getFixedDiscount();
            }
            
            stmt.setString(1, member.getName());
            stmt.setString(2, discountType);
            stmt.setDouble(3, discountValue);
            
            stmt.executeUpdate();
            
            // Set the database ID for the member
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    member.setDbId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error creating member: " + e.getMessage());
        }
    }

    @Override
    public Member findById(Integer id) {
        String query = "SELECT * FROM members WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String name = rs.getString("name");
                String discountType = rs.getString("discount_type");
                double discountValue = rs.getDouble("discount_value");
                
                Member member = MemberFactory.createMember(name, discountType, discountValue);
                member.setDbId(rs.getInt("id"));
                return member;
            }
        } catch (SQLException e) {
            System.err.println("Error finding member: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Member> findAll() {
        List<Member> members = new ArrayList<>();
        String query = "SELECT * FROM members";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                String name = rs.getString("name");
                String discountType = rs.getString("discount_type");
                double discountValue = rs.getDouble("discount_value");
                
                Member member = MemberFactory.createMember(name, discountType, discountValue);
                member.setDbId(rs.getInt("id"));
                members.add(member);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving members: " + e.getMessage());
        }
        
        return members;
    }
    
    @Override
    public List<Member> findActiveMembers() {
        List<Member> members = new ArrayList<>();
        // Subquery to find active members (who have borrowed at least one book)
        String query = "SELECT m.* FROM members m WHERE m.id IN " +
                      "(SELECT DISTINCT member_id FROM loans)";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            while (rs.next()) {
                String name = rs.getString("name");
                String discountType = rs.getString("discount_type");
                double discountValue = rs.getDouble("discount_value");
                
                Member member = MemberFactory.createMember(name, discountType, discountValue);
                member.setDbId(rs.getInt("id"));
                members.add(member);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving active members: " + e.getMessage());
        }
        
        return members;
    }

    @Override
    public void update(Member member) {
        String query = "UPDATE members SET name = ?, discount_type = ?, discount_value = ? WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            String discountType = "";
            double discountValue = 0.0;
            
            if (member.getPriceStrategy() instanceof CodeDiscountStrategy) {
                discountType = "code";
                CodeDiscountStrategy strategy = (CodeDiscountStrategy) member.getPriceStrategy();
                discountValue = strategy.getPercentageOff();
            } else if (member.getPriceStrategy() instanceof MemberDiscountStrategy) {
                discountType = "fixed";
                MemberDiscountStrategy strategy = (MemberDiscountStrategy) member.getPriceStrategy();
                discountValue = strategy.getFixedDiscount();
            }
            
            stmt.setString(1, member.getName());
            stmt.setString(2, discountType);
            stmt.setDouble(3, discountValue);
            stmt.setInt(4, member.getDbId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating member: " + e.getMessage());
        }
    }

    @Override
    public void delete(Integer id) {
        String query = "DELETE FROM members WHERE id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error deleting member: " + e.getMessage());
        }
    }
}