package org.whispersystems.circleservice.internal.push;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import org.whispersystems.libsignal.IdentityKey;
import org.whispersystems.circleservice.api.push.SignedPreKeyEntity;
import org.whispersystems.circleservice.internal.util.JsonUtil;

import java.util.List;

public class PreKeyState {

  @JsonProperty
  @JsonSerialize(using = JsonUtil.IdentityKeySerializer.class)
  @JsonDeserialize(using = JsonUtil.IdentityKeyDeserializer.class)
  private IdentityKey        identityKey;

  @JsonProperty
  private List<PreKeyEntity> preKeys;

  @JsonProperty
  private SignedPreKeyEntity signedPreKey;


  public PreKeyState(List<PreKeyEntity> preKeys, SignedPreKeyEntity signedPreKey, IdentityKey identityKey) {
    this.preKeys       = preKeys;
    this.signedPreKey  = signedPreKey;
    this.identityKey   = identityKey;
  }

}
