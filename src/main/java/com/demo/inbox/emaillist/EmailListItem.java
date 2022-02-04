package com.demo.inbox.emaillist;

import lombok.Data;
import org.springframework.data.annotation.Transient;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.CassandraType.Name;
import org.springframework.data.cassandra.core.mapping.Column;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.util.List;

@Table(value = "messages_by_user_folder")
@Data
public class EmailListItem {

    @PrimaryKey
    private EmailListItemKey key;

    @CassandraType(type = Name.LIST, typeArguments = Name.TEXT)
    private List<String> to;

    @CassandraType(type = Name.TEXT)
    private String subject;

    @Column(value = "is_read")
    @CassandraType(type = Name.BOOLEAN)
    private boolean isRead;

    @Transient
    private String agoTimeString;
}
