/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.bigtop.manager.server.command.stage.factory;

import org.apache.bigtop.manager.server.holder.SpringContextHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StageFactories {

    private static final AtomicBoolean LOADED = new AtomicBoolean(false);

    private static final Map<StageType, String> STAGE_FACTORIES = new HashMap<>();

    public static StageFactory getStageFactory(StageType stageType) {
        if (!LOADED.get()) {
            load();
        }

        String beanName = STAGE_FACTORIES.get(stageType);
        return SpringContextHolder.getApplicationContext().getBean(beanName, StageFactory.class);
    }

    private static synchronized void load() {
        if (LOADED.get()) {
            return;
        }

        for (Map.Entry<String, StageFactory> entry : SpringContextHolder.getStageFactories().entrySet()) {
            String beanName = entry.getKey();
            StageFactory stageFactory = entry.getValue();
            if (STAGE_FACTORIES.containsKey(stageFactory.getStageType())) {
                log.error("Duplicate StageLifecycle with type: {}", stageFactory.getStageType());
                continue;
            }

            STAGE_FACTORIES.put(stageFactory.getStageType(), beanName);
            log.info("Load StageLifecycle: {} with type: {}", stageFactory.getClass().getName(),
                    stageFactory.getStageType());
        }

        LOADED.set(true);
    }
}