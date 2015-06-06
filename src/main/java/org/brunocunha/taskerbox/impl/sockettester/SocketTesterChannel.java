package org.brunocunha.taskerbox.impl.sockettester;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j;

import org.brunocunha.sockettester.service.SocketTester;
import org.brunocunha.sockettester.vo.SocketTesterVO;
import org.brunocunha.taskerbox.core.TaskerboxChannel;
import org.brunocunha.taskerbox.core.annotation.TaskerboxField;

@Log4j
public class SocketTesterChannel extends TaskerboxChannel<SocketTesterVO> {

	@TaskerboxField("Tipo")
	@Getter @Setter
	private String type;
	
	@TaskerboxField("Nome")
	@Getter @Setter
	private String name;
	
	@TaskerboxField("Host")
	@Getter @Setter
	private String host;
	
	@TaskerboxField("Porta")
	@Getter @Setter
	private int port;
	
	@TaskerboxField("Servico")
	@Getter @Setter
	private String servico;
	
	@Getter @Setter
	private int typeValue;
	
	@Getter @Setter
	private String status;

	@Override
	public void setup() {
		if (this.type == null) {
			this.type = "http";
			this.typeValue = SocketTesterVO.SOCKET_TYPE;
			return;
		}

		if (this.type.equals("http")) {
			this.typeValue = SocketTesterVO.HTTP_TYPE;
		} else if (this.type.equals("appserver")) {
			this.typeValue = SocketTesterVO.APPSERVER_TYPE;
		} else if (this.type.equals("pf")) {
			this.typeValue = SocketTesterVO.PF_TYPE;
		} else {
			this.typeValue = SocketTesterVO.SOCKET_TYPE;
		}
	}

	@Override
	protected void execute() throws Exception {

		SocketTesterVO vo = new SocketTesterVO();
		vo.setName(name);
		vo.setHost(host);
		vo.setPort(port);
		vo.setServico(servico);
		vo.setStatus(status);
		vo.setType(typeValue);

		log.debug("Validating service " + id + " - " + vo);
		SocketTester.validateGeneric(vo);

		if (!vo.isValid()) {
			perform(vo);
		}
	}

	@Override
	protected String getItemFingerprint(SocketTesterVO entry) {
		return entry.toString();
	}

	@Override
	public String getDisplayName() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getId());
		if (this.getName() != null && !this.getName().equals("")) {
			sb.append(" (").append(this.getName()).append(")");
		}
		return sb.toString();
	}

	@Override
	public String toString() {
		return "SocketTesterChannel [type=" + type + ", name=" + name + ", host=" + host + ", port=" + port
				+ ", status=" + status + ", servico=" + servico + "]";
	}

}
