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

package io.fabric8.openshift.client.dsl.internal;

import com.squareup.okhttp.OkHttpClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.base.OperationSupport;
import io.fabric8.openshift.api.model.LocalSubjectAccessReview;
import io.fabric8.openshift.api.model.LocalSubjectAccessReviewBuilder;
import io.fabric8.openshift.api.model.SubjectAccessReview;
import io.fabric8.openshift.api.model.SubjectAccessReviewBuilder;
import io.fabric8.openshift.api.model.SubjectAccessReviewResponse;
import io.fabric8.openshift.client.OpenShiftConfig;
import io.fabric8.openshift.client.dsl.ClientSubjectAccessReviewOperation;
import io.fabric8.openshift.client.dsl.CreateableLocalSubjectAccessReview;
import io.fabric8.openshift.client.dsl.CreateableSubjectAccessReview;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class ClientSubjectAccessReviewOperationImpl extends OperationSupport implements ClientSubjectAccessReviewOperation<CreateableSubjectAccessReview, CreateableLocalSubjectAccessReview> {


  public ClientSubjectAccessReviewOperationImpl(OkHttpClient client, OpenShiftConfig config) {
    this(client, config, null);
  }

  public ClientSubjectAccessReviewOperationImpl(OkHttpClient client, OpenShiftConfig config, String namespace) {
    super(client, config, "subjectaccessreviews", namespace, null);
  }

  @Override
  public CreateableLocalSubjectAccessReview inNamespace(String namespace) {
    return new ClientSubjectAccessReviewOperationImpl(client, OpenShiftConfig.wrap(getConfig()), namespace).local();
  }

  @Override
  public SubjectAccessReviewResponse create(SubjectAccessReview... item) {
    return new CreateableSubjectAccessReviewImpl(client).create(item);
  }

  @Override
  public CreateableSubjectAccessReview createNew() {
    return new CreateableSubjectAccessReviewImpl(client).createNew();
  }

  @Override
  public URL getRootUrl() {
    try {
      return new URL(OpenShiftConfig.wrap(getConfig()).getOpenShiftUrl());
    } catch (MalformedURLException e) {
      throw KubernetesClientException.launderThrowable(e);
    }
  }


  private class CreateableLocalSubjectAccessReviewImpl extends CreateableLocalSubjectAccessReview {
    private final OkHttpClient client;
    private final LocalSubjectAccessReviewBuilder builder;

    private CreateableLocalSubjectAccessReviewImpl(OkHttpClient client) {
      this.client = client;
      this.builder = new LocalSubjectAccessReviewBuilder(CreateableLocalSubjectAccessReviewImpl.this);
    }

    private CreateableLocalSubjectAccessReviewImpl(OkHttpClient client, LocalSubjectAccessReviewBuilder builder) {
      this.client = client;
      this.builder = builder;
    }

    @Override
    public SubjectAccessReviewResponse create(LocalSubjectAccessReview... resources) {
      try {
        if (resources.length > 1) {
          throw new IllegalArgumentException("Too many items to create.");
        } else if (resources.length == 1) {
          return handleCreate(resources[0], SubjectAccessReviewResponse.class);
        } else {
          throw new IllegalArgumentException("Nothing to create.");
        }
      } catch (InterruptedException | ExecutionException | IOException e) {
        throw KubernetesClientException.launderThrowable(e);
      }
    }

    @Override
    public CreateableLocalSubjectAccessReview createNew() {
      return this;
    }

    @Override
    public SubjectAccessReviewResponse done() {
      return create(builder.build());
    }
  }

  private CreateableLocalSubjectAccessReview local() {
    return new CreateableLocalSubjectAccessReviewImpl(client);
  }

  private class CreateableSubjectAccessReviewImpl extends CreateableSubjectAccessReview {
    private final OkHttpClient client;
    private final SubjectAccessReviewBuilder builder;

    private CreateableSubjectAccessReviewImpl(OkHttpClient client) {
      this.client = client;
      this.builder = new SubjectAccessReviewBuilder(CreateableSubjectAccessReviewImpl.this);
    }

    private CreateableSubjectAccessReviewImpl(OkHttpClient client, SubjectAccessReviewBuilder builder) {
      this.client = client;
      this.builder = builder;
    }

    @Override
    public SubjectAccessReviewResponse create(SubjectAccessReview... resources) {
      try {
        if (resources.length > 1) {
          throw new IllegalArgumentException("Too many items to create.");
        } else if (resources.length == 1) {
          return handleCreate(resources[0], SubjectAccessReviewResponse.class);
        } else {
          throw new IllegalArgumentException("Nothing to create.");
        }
      } catch (InterruptedException | ExecutionException | IOException e) {
        throw KubernetesClientException.launderThrowable(e);
      }
    }

    @Override
    public CreateableSubjectAccessReview createNew() {
      return this;
    }

    @Override
    public SubjectAccessReviewResponse done() {
      return create(builder.build());
    }
  }
}
