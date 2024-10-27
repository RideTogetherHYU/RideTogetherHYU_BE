package com.ridetogether.server.domain.image.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.ridetogether.server.domain.image.model.ImageType;
import com.ridetogether.server.domain.member.domain.Member;
import com.ridetogether.server.domain.report.domain.Report;
import com.ridetogether.server.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@Table(name = "Image")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "image_idx")
	private Long idx;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_idx")
	private Member member;

	@Column(length = 1000)
	private String accessUri;

	private String imgUrl;

	private String parId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "report_idx")
	private Report report;

	@Enumerated(EnumType.STRING)
	private ImageType imageType;

	public void updateAccessUri(String accessUri) {
		this.accessUri = accessUri;
	}

	public void updateParId(String parId) {
		this.parId = parId;
	}

	public void updateImageType(ImageType imageType) {
		this.imageType = imageType;
	}

}
