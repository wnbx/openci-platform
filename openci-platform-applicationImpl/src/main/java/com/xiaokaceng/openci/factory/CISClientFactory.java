package com.xiaokaceng.openci.factory;

import java.util.HashSet;
import java.util.Set;

import org.openkoala.opencis.api.CISClient;

import com.xiaokaceng.openci.CISClientNotInstanceException;
import com.xiaokaceng.openci.domain.ToolConfiguration;
import com.xiaokaceng.openci.pojo.GitConfigurationPojo;
import com.xiaokaceng.openci.pojo.JenkinsConfigurationPojo;
import com.xiaokaceng.openci.pojo.JiraConfigurationPojo;
import com.xiaokaceng.openci.pojo.SonarConfigurationPojo;
import com.xiaokaceng.openci.pojo.SvnConfigurationPojo;
import com.xiaokaceng.openci.pojo.ToolConfigurationPojo;
import com.xiaokaceng.openci.pojo.TracConfigurationPojo;

public class CISClientFactory {

	private static Set<ToolConfigurationPojo> toolConfigurationPojos = new HashSet<ToolConfigurationPojo>();
	
	// TODO 这种方式无法解决各工具配置的差异，比如Jenkins需要ScmConfig
	static {
		toolConfigurationPojos.add(new SvnConfigurationPojo());
		toolConfigurationPojos.add(new GitConfigurationPojo());
		toolConfigurationPojos.add(new JenkinsConfigurationPojo());
		toolConfigurationPojos.add(new SonarConfigurationPojo());
		toolConfigurationPojos.add(new JiraConfigurationPojo());
		toolConfigurationPojos.add(new TracConfigurationPojo());
	}
	
	public synchronized static CISClient getInstance(ToolConfiguration toolConfiguration, Set<ToolConfigurationPojo> toolConfigurationPojos) {
		for (ToolConfigurationPojo each : toolConfigurationPojos) {
			each.createCISClient(toolConfiguration);
			if (each.isInstance()) {
				CISClient cisClient = each.getCISClient();
				each.destory();
				return cisClient;
			}
		}
		throw new CISClientNotInstanceException();
	}
	
}
