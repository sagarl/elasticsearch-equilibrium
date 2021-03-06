package com.sonian.elasticsearch.equilibrium;

import com.sonian.elasticsearch.tests.AbstractJettyHttpServerTests;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthStatus;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.ImmutableSettings;

/**
 * @author dakrone
 */
public class AbstractEquilibriumTests extends AbstractJettyHttpServerTests {

    public AbstractEquilibriumTests() {}

    // Helpers for tests

    public void createIndex(String id, String name, int numberOfShards, int numberOfRelicas) {
        Client c = client(id);
        c.admin().indices().prepareCreate(name)
                .setSettings(ImmutableSettings.settingsBuilder()
                        .put("number_of_shards", numberOfShards)
                        .put("number_of_replicas", numberOfRelicas))
                .execute().actionGet();
    }

    protected void deleteIndex(String id, String name) {
        try {
            client(id).admin().indices().prepareDelete(name).execute().actionGet();
        } catch (Exception e) {
            // ignore
        }
    }

    public ClusterHealthStatus getStatus(String id) {
        Client c = client(id);
        ClusterHealthResponse healthResponse = c.admin().cluster().prepareHealth().setTimeout("2s").execute().actionGet();
        return healthResponse.getStatus();
    }

    public boolean isGreen (String id) {
        return ClusterHealthStatus.GREEN == getStatus(id);
    }

    public boolean isYellow (String id) {
        return ClusterHealthStatus.YELLOW == getStatus(id);
    }

    public boolean isRed (String id) {
        return ClusterHealthStatus.RED == getStatus(id);
    }

    public void waitForGreen(String id, String idx, String timeout) {
        if (idx == null) {
            client(id).admin().cluster().prepareHealth().setWaitForGreenStatus().setTimeout(timeout).execute().actionGet();
        } else {
            client(id).admin().cluster().prepareHealth(idx).setWaitForGreenStatus().setTimeout(timeout).execute().actionGet();
        }
    }

    public void waitForYellow(String id, String idx, String timeout) {
        if (idx == null) {
            client(id).admin().cluster().prepareHealth().setWaitForYellowStatus().setTimeout(timeout).execute().actionGet();
        } else {
            client(id).admin().cluster().prepareHealth(idx).setWaitForYellowStatus().setTimeout(timeout).execute().actionGet();
        }
    }

}
