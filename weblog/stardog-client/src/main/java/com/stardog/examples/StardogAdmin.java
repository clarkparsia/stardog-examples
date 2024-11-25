package com.stardog.examples;

import java.util.function.Supplier;

import com.complexible.stardog.api.ConnectionCredentials;
import com.complexible.stardog.api.admin.AdminConnection;
import com.complexible.stardog.api.admin.AdminConnectionConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import com.complexible.stardog.search.SearchOptions;

public class StardogAdmin implements InitializingBean {

    final Logger log = LoggerFactory.getLogger(StardogAdmin.class);

    private String to;
    private String username;
    private String password;
    private String url;
    private Supplier<ConnectionCredentials> supplier;

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Supplier<ConnectionCredentials> getSupplier() {
		return supplier;
	}

	public void setSupplier(Supplier<ConnectionCredentials> supplier) {
		this.supplier = supplier;
	}

    /**
     *  Creates a connection to the DBMS itself so we can perform some administrative actions.
     */
    public void afterPropertiesSet() {
        if (username == null || password == null || to == null || url == null) {
            log.error("Invalid parameters");
            return;
        }

        try (final AdminConnection aConn = AdminConnectionConfiguration.toServer(url)
                .credentials(username, password)
                .connect()) {

            // A look at what databses are currently in Stardog - needed api and http
            aConn.list().forEach(item -> System.out.println(item));

            // Checks to see if the 'myNewDB' is in Stardog. If it is, we are going to drop it so we are
            // starting fresh
            if (aConn.list().contains(to)) {
                aConn.drop(to);
            }

            // Convenience function for creating a non-persistent in-memory database with all the default settings.
            aConn.newDatabase(to).set(SearchOptions.SEARCHABLE, true).create();
            aConn.close();
        }
    }
}
