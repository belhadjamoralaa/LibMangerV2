package librarymanagement;

import java.util.List;

/**
 * Interface for Member data access operations
 */
public interface MemberDAO extends DAO<Member, Integer> {
    /**
     * Find active members (who have borrowed at least one book)
     * @return A list of active members
     */
    List<Member> findActiveMembers();
}