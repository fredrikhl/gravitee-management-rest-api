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
package io.gravitee.management.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonpatch.diff.JsonDiff;
import io.gravitee.common.utils.UUID;
import io.gravitee.management.service.AuditService;
import io.gravitee.repository.exceptions.TechnicalException;
import io.gravitee.repository.management.api.AuditRepository;
import io.gravitee.repository.management.model.Audit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;

/**
 * @author Nicolas GERAUD (nicolas.geraud at graviteesource.com)
 * @author GraviteeSource Team
 */
@Component
public class AuditServiceImpl extends AbstractService implements AuditService {

    private final Logger LOGGER = LoggerFactory.getLogger(AuditServiceImpl.class);

    @Autowired
    private AuditRepository auditRepository;

    @Autowired
    private ObjectMapper mapper;

    public void createApiAuditLog(String apiId, Map properties, Audit.AuditEvent event, Date createdAt,
                                  Object oldValue, Object newValue) {
        create(Audit.AuditReferenceType.API,
                apiId,
                properties,
                event,
                getAuthenticatedUsername(),
                createdAt==null ? new Date() : createdAt,
                oldValue,
                newValue);
    }


    @Async
    protected void create(Audit.AuditReferenceType referenceType, String referenceId, Map properties,
                          Audit.AuditEvent event, String username, Date createdAt,
                          Object oldValue, Object newValue) {

        Audit audit = new Audit();
        audit.setId(UUID.toString(UUID.random()));
        audit.setUsername(username);
        audit.setCreatedAt(createdAt);
        audit.setProperties(properties);
        audit.setReferenceType(referenceType);
        audit.setReferenceId(referenceId);
        audit.setEvent(event.name());

        ObjectNode oldNode = oldValue == null
                ? mapper.createObjectNode()
                : mapper.convertValue(oldValue, ObjectNode.class).remove(Arrays.asList("updatedAt", "createdAt"));
        ObjectNode newNode = newValue == null
                ? mapper.createObjectNode()
                : mapper.convertValue(newValue, ObjectNode.class).remove(Arrays.asList("updatedAt", "createdAt"));

        audit.setPatch(JsonDiff.asJson(oldNode, newNode).toString());

        try {
            auditRepository.create(audit);
        } catch (TechnicalException e) {
            LOGGER.error("Error occurs during the creation of an Audit Log {}.", e);
        }
    }
}
