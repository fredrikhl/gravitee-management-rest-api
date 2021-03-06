/**
 * Copyright (C) 2015 The Gravitee team (http://gravitee.io)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.gravitee.management.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * @author Titouan COMPIEGNE
 */
public class UpdatePageEntity {

	@NotNull
	@Size(min = 1)
	private String name;

	private String content;

	private String lastContributor;

	private int order;

	private boolean published;

	private PageSourceEntity source;

	private PageConfigurationEntity configuration;

	private boolean homepage;

	@JsonProperty("excluded_groups")
	private List<String> excludedGroups;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLastContributor() {
		return lastContributor;
	}

	public void setLastContributor(String lastContributor) {
		this.lastContributor = lastContributor;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public PageSourceEntity getSource() {
		return source;
	}

	public void setSource(PageSourceEntity source) {
		this.source = source;
	}

	public PageConfigurationEntity getConfiguration() {
		return configuration;
	}

	public void setConfiguration(PageConfigurationEntity configuration) {
		this.configuration = configuration;
	}

	public boolean isHomepage() {
		return homepage;
	}

	public void setHomepage(boolean homepage) {
		this.homepage = homepage;
	}

	public List<String> getExcludedGroups() {
		return excludedGroups;
	}

	public void setExcludedGroups(List<String> excludedGroups) {
		this.excludedGroups = excludedGroups;
	}
}
