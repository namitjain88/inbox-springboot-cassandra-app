package com.demo.inbox.emaillist;

import org.springframework.data.cassandra.repository.CassandraRepository;

import java.util.List;

public interface EmailItemListRepository extends CassandraRepository<EmailListItem, EmailListItemKey> {

    // Look up userId and Label in class type of key(EmailListItemKey) property in EmailListItem
    List<EmailListItem> findAllByKey_UserIdAndKey_Label(String userId, String label);
}
