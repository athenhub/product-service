package com.athenhub.productservice.membership.domain;

import java.util.List;

public record MemberRoles(List<MemberRole> roles) {
  public static MemberRoles of(List<MemberRole> memberRoles) {
    return new MemberRoles(memberRoles);
  }

  public boolean containsMasterManager() {
    return roles.contains(MemberRole.MASTER_MANAGER);
  }

  public boolean containsHubManager() {
    return roles.contains(MemberRole.HUB_MANAGER);
  }

  public boolean containsVendorAgent() {
    return roles.contains(MemberRole.VENDOR_AGENT);
  }
}
