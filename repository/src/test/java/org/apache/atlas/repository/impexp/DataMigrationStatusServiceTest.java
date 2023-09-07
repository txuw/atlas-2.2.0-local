/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.atlas.repository.impexp;

import com.google.inject.Inject;
import org.apache.atlas.TestModules;
import org.apache.atlas.exception.AtlasBaseException;
import org.apache.atlas.model.migration.MigrationImportStatus;
import org.apache.atlas.repository.graphdb.AtlasGraph;
import org.apache.atlas.repository.migration.DataMigrationStatusService;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;

import java.util.Date;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertNull;

@Guice(modules = TestModules.TestOnlyModule.class)
public class DataMigrationStatusServiceTest {
    @Inject
    AtlasGraph atlasGraph;

    @Test
    public void createUpdateDelete() {
        final String STATUS_DONE = "DONE";

        DataMigrationStatusService dataMigrationStatusService = new DataMigrationStatusService(atlasGraph);

        MigrationImportStatus expected = new MigrationImportStatus("/tmp/defg.zip");
        expected.setTotalCount(3333);
        expected.setCurrentIndex(20);
        expected.setStartTime(new Date());

        MigrationImportStatus ret = dataMigrationStatusService.getCreate(expected);

        assertNotNull(ret);
        assertEquals(ret.getName(), expected.getName());
        assertEquals(ret.getStartTime(), expected.getStartTime());
        assertEquals(ret.getTotalCount(), expected.getTotalCount());
        assertEquals(ret.getCurrentIndex(), expected.getCurrentIndex());

        dataMigrationStatusService.savePosition(100l);
        assertNotNull(dataMigrationStatusService.getStatus());
        assertNotNull(dataMigrationStatusService.getStatus().getCurrentIndex(), "100");
        assertNotNull(dataMigrationStatusService.getCreate(expected).getCurrentIndex(), "100");


        dataMigrationStatusService.setStatus(STATUS_DONE);
        assertNotNull(dataMigrationStatusService.getCreate(expected).getOperationStatus());
        assertEquals(dataMigrationStatusService.getCreate(expected).getOperationStatus(), STATUS_DONE);

        dataMigrationStatusService.delete();
        assertNull(dataMigrationStatusService.getStatus());
    }
}
