package com.cwb.content.model.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 
 * @TableName teachplan_work
 */
@TableName(value ="teachplan_work")
@Data
public class TeachplanWork implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 作业信息标识
     */
    private Long workId;

    /**
     * 作业标题
     */
    private String workTitle;

    /**
     * 课程计划标识
     */
    private Long teachplanId;

    /**
     * 课程标识
     */
    private Long courseId;

    /**
     * 
     */
    private Date createDate;

    /**
     * 
     */
    private Long coursePubId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        TeachplanWork other = (TeachplanWork) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
            && (this.getWorkId() == null ? other.getWorkId() == null : this.getWorkId().equals(other.getWorkId()))
            && (this.getWorkTitle() == null ? other.getWorkTitle() == null : this.getWorkTitle().equals(other.getWorkTitle()))
            && (this.getTeachplanId() == null ? other.getTeachplanId() == null : this.getTeachplanId().equals(other.getTeachplanId()))
            && (this.getCourseId() == null ? other.getCourseId() == null : this.getCourseId().equals(other.getCourseId()))
            && (this.getCreateDate() == null ? other.getCreateDate() == null : this.getCreateDate().equals(other.getCreateDate()))
            && (this.getCoursePubId() == null ? other.getCoursePubId() == null : this.getCoursePubId().equals(other.getCoursePubId()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getWorkId() == null) ? 0 : getWorkId().hashCode());
        result = prime * result + ((getWorkTitle() == null) ? 0 : getWorkTitle().hashCode());
        result = prime * result + ((getTeachplanId() == null) ? 0 : getTeachplanId().hashCode());
        result = prime * result + ((getCourseId() == null) ? 0 : getCourseId().hashCode());
        result = prime * result + ((getCreateDate() == null) ? 0 : getCreateDate().hashCode());
        result = prime * result + ((getCoursePubId() == null) ? 0 : getCoursePubId().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", workId=").append(workId);
        sb.append(", workTitle=").append(workTitle);
        sb.append(", teachplanId=").append(teachplanId);
        sb.append(", courseId=").append(courseId);
        sb.append(", createDate=").append(createDate);
        sb.append(", coursePubId=").append(coursePubId);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}