/*
 * Copyright 2019 Laurent PAGE, Apache Licence 2.0
 */
package live.page.web.system.db;

import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.BsonField;
import live.page.web.system.json.Json;

import java.util.ArrayList;
import java.util.List;

/**
 * The best class for save time
 * provide utils for Mongodb Aggregation
 */
public class Aggregator extends ArrayList<BsonField> {

	private final List<String> keys = new ArrayList<>();

	/**
	 * Constructor
	 *
	 * @param keys needed for result, keys who must be preserve
	 */
	public Aggregator(String... keys) {
		for (String key : keys) {
			if (key != null) {
				this.keys.add(key);
			}
		}
	}

	/**
	 * Add key after constructor
	 *
	 * @param key to add
	 * @return this Aggregator
	 */
	public Aggregator addKey(String key) {
		keys.add(key);
		return this;
	}

	/**
	 * Get the base Accumulator and all other provided or have to be replaced
	 *
	 * @param groups accumulators to add or replace
	 * @return a list of accumulators
	 */
	public List<BsonField> getGrouper(BsonField... groups) {
		List<BsonField> grouper = new ArrayList<>();
		List<String> addkeys = new ArrayList<>();
		for (BsonField group : groups) {
			grouper.add(group);
			addkeys.add(group.getName());
		}
		for (String key : keys) {
			if (!addkeys.contains(key)) {
				grouper.add(Accumulators.first(key, "$" + key));
			}
		}
		return grouper;
	}

	/**
	 * Get the base Projection Json/Bson
	 *
	 * @return a base projection object to preserve, user .remove(key) for remove
	 */
	public Json getProjection() {
		Json projection = new Json();
		for (String key : keys) {
			projection.put(key, true);
		}
		return projection;
	}

	/**
	 * Guarantee the order of a projection
	 *
	 * @return a base projection object to preserve and order
	 */
	public Json getProjectionOrder() {
		Json projection = new Json("_id", "$_id");
		for (String key : keys) {
			projection.put(key, "$" + key);
		}
		return projection;
	}

	/**
	 * A simple clone function
	 *
	 * @return a clone
	 */
	@Override
	public Aggregator clone() {
		return new Aggregator(keys.toArray(new String[0]));
	}
}
