/**
 * Copyright (C) 2015 Red Hat, Inc.
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
package io.fabric8.openshift.client.handlers;

import com.squareup.okhttp.OkHttpClient;
import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ResourceHandler;
import io.fabric8.openshift.api.model.PolicyBinding;
import io.fabric8.openshift.client.OpenShiftConfig;
import io.fabric8.openshift.client.dsl.internal.PolicyBindingOperationsImpl;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;

@Component
@Service
public class PolicyBindingHandler implements ResourceHandler<PolicyBinding> {

  @Override
  public String getKind() {
    return PolicyBinding.class.getSimpleName();
  }

  @Override
  public PolicyBinding create(OkHttpClient client, Config config, String namespace, PolicyBinding item) {
      return new PolicyBindingOperationsImpl(client, OpenShiftConfig.wrap(config), namespace, null, true, item).create();
  }

  @Override
  public Boolean delete(OkHttpClient client, Config config, String namespace, PolicyBinding item) {
      return new PolicyBindingOperationsImpl(client, OpenShiftConfig.wrap(config), namespace, null, true, item).delete(item);
    }
}
