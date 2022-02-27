package com.demo.inbox.folder;

import java.util.List;

import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.data.cassandra.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UnreadEmailCountRepository extends CassandraRepository<UnreadEmailCount, String> {

    public List<UnreadEmailCount> findAllByUserId(String userId);

    @Query(value = "update unread_email_count_by_user_folder set unread_email_count = unread_email_count + 1 where user_id=?0 and label=?1")
    public void incrementUnreadEmailCount(String userId, String label);

    @Query(value = "update unread_email_count_by_user_folder set unread_email_count = unread_email_count - 1 where user_id=?0 and label=?1")
    public void decrementUnreadEmailCount(String userId, String label);
}
