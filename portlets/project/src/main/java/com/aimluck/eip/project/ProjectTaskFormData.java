/*
 * Aipo is a groupware program developed by Aimluck,Inc.
 * Copyright (C) 2004-2013 Aimluck,Inc.
 * http://www.aipo.com
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Project Management Portlet was developed by Advance,Inc.
 * http://www.a-dvance.co.jp/
 */

package com.aimluck.eip.project;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.cayenne.DataRow;
import org.apache.cayenne.exp.ExpressionFactory;
import org.apache.jetspeed.services.logging.JetspeedLogFactoryService;
import org.apache.jetspeed.services.logging.JetspeedLogger;
import org.apache.turbine.util.RunData;
import org.apache.velocity.context.Context;

import com.aimluck.commons.field.ALDateField;
import com.aimluck.commons.field.ALNumberField;
import com.aimluck.commons.field.ALStringField;
import com.aimluck.commons.utils.ALDateUtil;
import com.aimluck.eip.cayenne.om.portlet.EipTProject;
import com.aimluck.eip.cayenne.om.portlet.EipTProjectTask;
import com.aimluck.eip.cayenne.om.portlet.EipTProjectTaskFile;
import com.aimluck.eip.cayenne.om.portlet.EipTProjectTaskMember;
import com.aimluck.eip.cayenne.om.security.TurbineUser;
import com.aimluck.eip.common.ALAbstractFormData;
import com.aimluck.eip.common.ALDBErrorException;
import com.aimluck.eip.common.ALEipConstants;
import com.aimluck.eip.common.ALEipManager;
import com.aimluck.eip.common.ALEipPost;
import com.aimluck.eip.common.ALEipUser;
import com.aimluck.eip.common.ALPageNotFoundException;
import com.aimluck.eip.fileupload.beans.FileuploadBean;
import com.aimluck.eip.modules.actions.common.ALAction;
import com.aimluck.eip.orm.Database;
import com.aimluck.eip.orm.query.SelectQuery;
import com.aimluck.eip.project.util.ProjectFile;
import com.aimluck.eip.project.util.ProjectUtils;
import com.aimluck.eip.services.eventlog.ALEventlogConstants;
import com.aimluck.eip.services.eventlog.ALEventlogFactoryService;
import com.aimluck.eip.services.storage.ALStorageService;
import com.aimluck.eip.util.ALEipUtils;

/**
 *
 */
public class ProjectTaskFormData extends ALAbstractFormData {

  /** logger */
  private static final JetspeedLogger logger = JetspeedLogFactoryService
    .getLogger(ProjectTaskFormData.class.getName());

  /** 分類 */
  private ALStringField tracker;

  /** タスク名 */
  private ALStringField task_name;

  /** 説明 */
  private ALStringField explanation;

  /** ステータス */
  private ALStringField status;

  /** 優先度 */
  private ALStringField priority;

  /** 開始予定日 */
  private ALDateField start_plan_date;

  /** 完了予定日 */
  private ALDateField end_plan_date;

  /** 開始実績日 */
  private ALDateField start_date;

  /** 完了実績日 */
  private ALDateField end_date;

  /** 開始予定日指定フラグ */
  private ALStringField start_plan_date_check;

  /** 完了予定日指定フラグ */
  private ALStringField end_plan_date_check;

  /** 開始実績日指定フラグ */
  private ALStringField start_date_check;

  /** 完了実績日指定フラグ */
  private ALStringField end_date_check;

  /** 計画工数 */
  private BigDecimal plan_workload;

  /** 計画工数（入力値用） */
  private String planWorkloadString;

  /** 進捗率 */
  private ALNumberField progress_rate;

  /** プロジェクトID */
  private Integer projectId;

  /** タスクID */
  private Integer taskId;

  /** プロジェクトオブジェクト */
  private EipTProject project;

  /** ログインユーザー */
  private ALEipUser loginUser;

  /** 親タスクID */
  private Integer parentTaskId;

  /** 子タスク保持フラグ */
  private boolean hasChildren;

  /** 担当者リスト */
  private List<ProjectTaskMemberResultData> taskMembers;

  /** プロジェクトメンバーリスト */
  private List<ALEipUser> projectMembers;

  /** ファイルアップロードリスト */
  private List<FileuploadBean> fileuploadList;

  /** 添付フォルダ名 */
  private String folderName;

  private int uid;

  /** ファイル操作オブジェクト */
  private final ProjectFile<EipTProjectTaskFile, EipTProjectTask> pfile =
    new ProjectFile<EipTProjectTaskFile, EipTProjectTask>(
      EipTProjectTaskFile.class);

  /**
   * 初期設定
   * 
   * @param action
   *          ALAction
   * @param rundata
   *          RunData
   * @param context
   *          Context
   */
  @Override
  public void init(ALAction action, RunData rundata, Context context)
      throws ALPageNotFoundException, ALDBErrorException {
    super.init(action, rundata, context);

    rundata.getUser().setTemp(ALEipConstants.MYGROUP, null);

    try {
      parentTaskId =
        Integer.valueOf(rundata.getParameters().getString("parent_task_id"));
    } catch (NumberFormatException e) {
      parentTaskId = null;
    }
    projectId =
      Integer.valueOf(ALEipUtils.getParameter(rundata, context, "projectid"));
    project = ProjectUtils.getEipTProject(projectId);
    loginUser = ALEipUtils.getALEipUser(rundata);
    folderName = rundata.getParameters().getString("folderName");
    uid = ALEipUtils.getUserId(rundata);
    projectMembers = ProjectUtils.getProjectMembers(projectId);

    try {
      taskId =
        Integer.valueOf(ALEipUtils.getTemp(
          rundata,
          context,
          ALEipConstants.ENTITY_ID));
    } catch (NumberFormatException e) {
      taskId = null;
    }

    // 子タスク保持フラグ
    hasChildren = (ProjectUtils.getCountChildrenTask(taskId) > 0);
  }

  /**
   * 各フィールドを初期化します。
   */
  @Override
  public void initField() {
    // 分類
    tracker = new ALStringField();
    tracker.setFieldName("分類");
    tracker.setNotNull(true);
    // タスク名
    task_name = new ALStringField();
    task_name.setFieldName("タスク名");
    task_name.setTrim(true);
    task_name.setNotNull(true);

    // 説明
    explanation = new ALStringField();
    explanation.setFieldName("説明");
    explanation.setTrim(true);
    // ステータス
    status = new ALStringField();
    status.setFieldName("ステータス");
    status.setNotNull(true);
    // 優先度
    priority = new ALStringField();
    priority.setFieldName("優先度");
    priority.setNotNull(true);
    // 開始予定日
    start_plan_date = new ALDateField();
    start_plan_date.setFieldName("開始予定日");
    start_plan_date.setValue(new Date());
    // 完了予定日
    end_plan_date = new ALDateField();
    end_plan_date.setFieldName("完了予定日");
    end_plan_date.setValue(new Date());
    // 開始実績日
    start_date = new ALDateField();
    start_date.setFieldName("開始実績日");
    start_date.setValue(new Date());
    // 完了実績日
    end_date = new ALDateField();
    end_date.setFieldName("完了実績日");
    end_date.setValue(new Date());
    // 開始予定日指定フラグ
    start_plan_date_check = new ALStringField();
    start_plan_date_check.setFieldName("指定しない");
    // 締切予定日指定フラグ
    end_plan_date_check = new ALStringField();
    end_plan_date_check.setFieldName("指定しない");
    // 開始実績日指定フラグ
    start_date_check = new ALStringField();
    start_date_check.setFieldName("指定しない");
    // 締切実績日指定フラグ
    end_date_check = new ALStringField();
    end_date_check.setFieldName("指定しない");
    // 計画工数
    plan_workload = new BigDecimal(0);
    // 計画工数（入力値用）
    planWorkloadString = null;
    // 進捗率
    progress_rate = new ALNumberField();
    progress_rate.setFieldName("進捗率");
    progress_rate.setValue(0);
    progress_rate.limitMinValue(0);
    progress_rate.limitMaxValue(100);
    // 子タスク保持フラグ
    hasChildren = false;
    // 担当者リスト
    taskMembers = new ArrayList<ProjectTaskMemberResultData>();
    // プロジェクトリスト
    projectMembers = new ArrayList<ALEipUser>();
    // ファイルリスト
    fileuploadList = new ArrayList<FileuploadBean>();
  }

  /**
   * タスクの各フィールドに対する制約条件を設定します。
   */
  @Override
  protected void setValidator() {
    // プロジェクト名必須項目
    task_name.setNotNull(true);
    // プロジェクト名の文字数制限
    task_name.limitMaxLength(50);
    // 進捗率の入力範囲制限
    progress_rate.limitMaxValue(100);
    progress_rate.limitMinValue(0);
  }

  /**
   * データに値を設定します。
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean setFormData(RunData rundata, Context context,
      List<String> msgList) throws ALPageNotFoundException, ALDBErrorException {
    boolean res = super.setFormData(rundata, context, msgList);
    try {
      if (res) {

        String members[] = rundata.getParameters().getStrings("task_member");
        String workload[] = rundata.getParameters().getStrings("workload");
        if (members != null) {
          for (int i = 0; i < members.length; i++) {
            if (members[i] == null || members[i].length() == 0) {
              continue;
            }

            // ユーザー情報
            ALEipUser user =
              ALEipUtils.getALEipUser(Integer.valueOf(members[i]));

            // 作業時間
            BigDecimal w = BigDecimal.valueOf(0);
            if (workload[i] != null && workload[i].length() > 0) {
              w = new BigDecimal(workload[i]);
            }

            ProjectTaskMemberResultData member =
              new ProjectTaskMemberResultData();
            member.initField();
            member.setUserId(user.getUserId().getValue());
            member.setUserName(user.getAliasName().getValue());
            member.setWorkload(w);

            taskMembers.add(member);
          }
        }

        planWorkloadString = rundata.getParameters().getString("plan_workload");

        // アップロードファイル
        fileuploadList = pfile.getFileuploadList(rundata);

        if (ALEipConstants.MODE_NEW_FORM.equals(getMode())) {
          String nullStr = null;
          start_date_check.setValue("TRUE");
          start_date.setValue(nullStr);
          end_date_check.setValue("TRUE");
          end_date.setValue(nullStr);
        }
      }
    } catch (Exception ex) {
      logger.error("Exception", ex);
      res = false;
    }
    return res;
  }

  /**
   * タスクのフォームに入力されたデータの妥当性検証を行います。
   * 
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean validate(List<String> msgList) {
    try {

      tracker.validate(msgList);
      task_name.validate(msgList);
      status.validate(msgList);
      priority.validate(msgList);
      progress_rate.validate(msgList);

      if (!hasChildren) {
        if (taskMembers.isEmpty()) {
          // 担当者が未入力の場合
          msgList.add("『 <span class='em'>担当者</span> 』を入力してください。");
        } else {
          // 担当者入力有の場合

          List<Long> checkMemberId = new ArrayList<Long>();
          for (Iterator<ProjectTaskMemberResultData> iter =
            taskMembers.iterator(); iter.hasNext();) {
            ProjectTaskMemberResultData data = iter.next();
            long id = data.getUserId().getValue();

            if (checkMemberId.contains(id)) {
              msgList.add("『 <span class='em'>担当者</span> 』が重複しています。");
              break;
            }

            checkMemberId.add(id);
          }
        }

        // 開始予定日＞完了予定日の場合エラー
        if (start_plan_date.getYear().length() > 0
          && end_plan_date.getYear().length() > 0
          && !ProjectUtils.isEmptyDate(start_plan_date.getValue().getDate())
          && !ProjectUtils.isEmptyDate(end_plan_date.getValue().getDate())) {

          Calendar start = Calendar.getInstance();
          Calendar end = Calendar.getInstance();
          start.setTime(start_plan_date.getValue().getDate());
          end.setTime(end_plan_date.getValue().getDate());

          if (start.compareTo(end) > 0) {
            msgList
              .add("『 <span class='em'>開始予定日</span> 』は『 <span class='em'>完了予定日</span> 』以前を入力してください。");
          }
        }

        // 開始実績日＞完了実績日の場合エラー
        if (start_date.getYear().length() > 0
          && end_date.getYear().length() > 0
          && !ProjectUtils.isEmptyDate(start_date.getValue().getDate())
          && !ProjectUtils.isEmptyDate(end_date.getValue().getDate())) {

          Calendar start = Calendar.getInstance();
          Calendar end = Calendar.getInstance();
          start.setTime(start_date.getValue().getDate());
          end.setTime(end_date.getValue().getDate());

          if (start.compareTo(end) > 0) {
            msgList
              .add("『 <span class='em'>開始実績日</span> 』は『 <span class='em'>完了実績日</span> 』以前を入力してください。");
          }
        }

        try {
          plan_workload = new BigDecimal(planWorkloadString);
          if (plan_workload.compareTo(BigDecimal.valueOf(0)) < 0) {
            msgList.add("『 <span class='em'>計画工数</span> 』には0以上の値を入力してください。");
          }
        } catch (Exception e) {
          msgList.add("『 <span class='em'>計画工数</span> 』は数値を入力してください。");
        }
      }

    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }

    return msgList.isEmpty();
  }

  /**
   * タスクをデータベースから読み出します。
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean loadFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTProjectTask task = ProjectUtils.getEipTProjectTask(rundata, context);
      if (task == null) {
        return false;
      }
      // 分類
      tracker.setValue(task.getTracker());
      // タスク名
      task_name.setValue(task.getTaskName());
      // 説明
      explanation.setValue(task.getExplanation());
      // ステータス
      status.setValue(task.getStatus());
      // 優先度
      priority.setValue(task.getPriority());
      // 開始予定日
      String nullStr = null;
      if (ProjectUtils.isEmptyDate(task.getStartPlanDate())) {
        start_plan_date_check.setValue("TRUE");
        start_plan_date.setValue(nullStr);
      } else {
        start_plan_date.setValue(task.getStartPlanDate());
      }
      // 完了予定日
      if (ProjectUtils.isEmptyDate(task.getEndPlanDate())) {
        end_plan_date_check.setValue("TRUE");
        end_plan_date.setValue(nullStr);
      } else {
        end_plan_date.setValue(task.getEndPlanDate());
      }
      // 開始実績日
      if (ProjectUtils.isEmptyDate(task.getStartDate())) {
        start_date_check.setValue("TRUE");
        start_date.setValue(nullStr);
      } else {
        start_date_check.setValue("FALSE");
        start_date.setValue(task.getStartDate());
      }
      // 完了実績日
      if (ProjectUtils.isEmptyDate(task.getEndDate())) {
        end_date_check.setValue("TRUE");
        end_date.setValue(nullStr);
      } else {
        end_date_check.setValue("FALSE");
        end_date.setValue(task.getEndDate());
      }
      // 計画工数
      plan_workload = task.getPlanWorkload();
      // 進捗率
      progress_rate.setValue(task.getProgressRate());

      // 担当者を取得
      taskMembers =
        ProjectUtils.getProjectTaskMembers(String.valueOf(task.getTaskId()));

      // ファイル
      List<EipTProjectTaskFile> files =
        pfile.getSelectQueryForFiles(
          EipTProjectTask.TASK_ID_PK_COLUMN,
          task.getTaskId()).fetchList();
      fileuploadList = pfile.getFileList(files);

    } catch (Exception ex) {
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * タスクをデータベースに格納します。
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean insertFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {

      // ログインユーザーの取得
      TurbineUser tuser =
        Database.get(TurbineUser.class, loginUser.getUserId());

      // 新規オブジェクトモデル
      EipTProjectTask task = Database.create(EipTProjectTask.class);

      // 登録・更新共通の項目の設定
      setInsertUpdate(task);

      // 作成者
      task.setTurbineUser(tuser);

      // 作成日
      task.setCreateDate(Calendar.getInstance().getTime());

      if (parentTaskId != null) {
        // 親タスクID
        task.setParentTaskId(Integer.valueOf(parentTaskId));
      }

      // ソートNoを取得
      int orderNo = 1;
      SelectQuery<EipTProjectTask> query =
        Database.query(EipTProjectTask.class);
      query.andQualifier(ExpressionFactory.matchExp(
        EipTProjectTask.PARENT_TASK_ID_PROPERTY,
        parentTaskId));
      query.orderDesending(EipTProjectTask.ORDER_NO_PROPERTY);
      EipTProjectTask parentTask = query.fetchSingle();
      if (parentTask != null) {
        orderNo = parentTask.getOrderNo() + 1;
      }
      task.setOrderNo(orderNo);

      // 担当者情報
      for (int i = 0; i < taskMembers.size(); i++) {
        ProjectTaskMemberResultData member = taskMembers.get(i);
        EipTProjectTaskMember data = new EipTProjectTaskMember();
        data.setEipTProjectTask(task);
        data.setUserId((int) member.getUserId().getValue());
        data.setWorkload(member.getWorkload());
      }

      // 添付ファイルを登録する。
      pfile.insertAttachmentFiles(
        fileuploadList,
        folderName,
        uid,
        task,
        msgList);

      // タスクを登録
      Database.commit();

      // 親タスク情報を更新
      updateParentTask(task.getParentTaskId());
      Database.commit();

      // プロジェクト情報を更新
      if (updateProject(project.getProjectId())) {
        Database.commit();
      }

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        task.getTaskId(),
        ALEventlogConstants.PORTLET_TYPE_NONE,
        "プロジェクト管理タスク 「" + task.getTaskName() + "」 追加");

    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * 登録・更新共通の項目を設定します。
   * 
   * @param task
   *          タスク
   * @param msgList
   *          メッセージリスト
   * @return TRUE:成功 FALSE:失敗
   * @throws Exception
   *           例外
   */
  private boolean setInsertUpdate(EipTProjectTask task) {

    // プロジェクトID
    task.setProject(project);
    // 分類
    task.setTracker(tracker.getValue());
    // タスク名
    task.setTaskName(task_name.getValue());
    // 説明
    task.setExplanation(explanation.getValue());
    // ステータス
    task.setStatus(status.getValue());
    // 優先度
    task.setPriority(priority.getValue());

    if (!hasChildren) {
      // 子タスクがない場合のみ更新する

      // 開始予定日
      task
        .setStartPlanDate(getInputDate(start_plan_date_check, start_plan_date));
      // 完了予定日
      task.setEndPlanDate(getInputDate(end_plan_date_check, end_plan_date));
      // 開始実績日
      task.setStartDate(getInputDate(start_date_check, start_date));
      // 完了実績日
      task.setEndDate(getInputDate(end_date_check, end_date));
      // 計画工数
      task.setPlanWorkload(plan_workload);
      // 進捗率
      task.setProgressRate((int) progress_rate.getValue());
    }
    // 更新者
    task.setUpdateUserId((int) loginUser.getUserId().getValue());
    // 更新日
    task.setUpdateDate(Calendar.getInstance().getTime());

    return true;
  }

  /**
   * 日付項目の値を取得する
   * 
   * @param check
   *          チェック項目
   * @param date
   *          日付項目
   * @return 入力値
   */
  private Date getInputDate(ALStringField check, ALDateField date) {
    try {
      if (check.getValue() == null) {
        return date.getValue().getDate();
      } else {
        return ProjectUtils.getEmptyDate();
      }
    } catch (Exception e) {
      return ProjectUtils.getEmptyDate();
    }
  }

  /**
   * 子タスクの値から親タスクの値を再計算し更新する。
   * 
   * @param taskId
   *          タスクID
   * @return FALSE:失敗
   */
  private boolean updateParentTask(Integer taskId) {

    if (taskId == null) {
      return false;
    }

    // オブジェクトモデルを取得
    EipTProjectTask task = ProjectUtils.getEipTProjectTask("" + taskId);
    if (task == null) {
      return false;
    }
    // 進捗情報取得
    List<DataRow> datarow = ProjectUtils.getChildTask(task.getTaskId());

    // 進捗情報設定
    if (datarow != null) {

      DataRow row = datarow.get(0);
      Object cnt = row.get("cnt");

      if (cnt != null && Integer.valueOf(cnt.toString()) > 0) {
        // 計算対象子タスクがある場合

        // 実績進捗率
        task.setProgressRate(Integer.valueOf(row.get("result_per").toString()));
        // 計画工数
        task
          .setPlanWorkload(new BigDecimal(row.get("plan_workload").toString()));
        // 開始予定日
        Object startPlanDate = row.get("start_plan_date");
        if (startPlanDate == null) {
          task.setStartPlanDate(ProjectUtils.getEmptyDate());
        } else {
          task.setStartPlanDate((Date) startPlanDate);
        }
        // 完了予定日
        Object endPlanDate = row.get("end_plan_date");
        if (endPlanDate == null) {
          task.setEndPlanDate(ProjectUtils.getEmptyDate());
        } else {
          task.setEndPlanDate((Date) endPlanDate);
        }
        // 開始実績日
        Object startDate = row.get("start_date");
        if (startDate == null) {
          task.setStartDate(ProjectUtils.getEmptyDate());
        } else {
          task.setStartDate((Date) startDate);
        }
        // 完了実績日
        Object endDate = row.get("end_date");
        if (endDate == null) {
          task.setEndDate(ProjectUtils.getEmptyDate());
        } else {
          task.setEndDate((Date) endDate);
        }

      } else {
        task.setStartPlanDate(ProjectUtils.getEmptyDate());
        task.setEndPlanDate(ProjectUtils.getEmptyDate());
        task.setStartDate(ProjectUtils.getEmptyDate());
        task.setEndDate(ProjectUtils.getEmptyDate());
        task.setPlanWorkload(BigDecimal.valueOf(0));
        task.setProgressRate(0);
      }
    }

    // 担当者情報を削除
    ProjectUtils.removeProjectTaskMember(task);

    // 担当者情報取得
    List<DataRow> memberRow = ProjectUtils.getChildTaskMember(taskId);

    // 担当者設定
    if (datarow != null) {
      for (int i = 0; i < memberRow.size(); i++) {
        DataRow member = memberRow.get(i);
        EipTProjectTaskMember data =
          Database.create(EipTProjectTaskMember.class);
        data.setEipTProjectTask(task);
        data.setUserId(Integer.valueOf(member.get("user_id").toString()));
        data.setWorkload(new BigDecimal(member.get("workload").toString()));
      }
    }
    // 親タスクを更新する
    return updateParentTask(task.getParentTaskId());
  }

  /**
   * プロジェクトを更新する
   * 
   * @param projectId
   *          プロジェクトID
   * @return TRUE:更新実行 FALSE:更新しない
   */
  private boolean updateProject(Integer projectId) {

    // オブジェクトモデルを取得
    EipTProject project = ProjectUtils.getEipTProject(projectId);
    if (project == null) {
      return false;
    }

    if (ProjectUtils.FLG_OFF.equals(project.getProgressFlg())) {
      // 自動計算しない場合
      return false;
    }

    project.setProgressRate(ProjectUtils.getProjectProgressRate(projectId));
    project.setUpdateUserId((int) loginUser.getUserId().getValue());
    project.setUpdateDate(Calendar.getInstance().getTime());

    return true;
  }

  /**
   * タスクを更新します。
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean updateFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTProjectTask task = ProjectUtils.getEipTProjectTask(rundata, context);
      if (task == null) {
        return false;
      }

      // 登録・更新共通の項目の設定
      setInsertUpdate(task);

      if (!hasChildren) {
        // 子タスクがない場合のみ更新する

        // 担当者情報を削除
        ProjectUtils.removeProjectTaskMember(task);

        // 担当者情報
        for (int i = 0; i < taskMembers.size(); i++) {
          ProjectTaskMemberResultData member = taskMembers.get(i);
          EipTProjectTaskMember data =
            Database.create(EipTProjectTaskMember.class);
          data.setEipTProjectTask(task);
          data.setUserId((int) member.getUserId().getValue());
          data.setWorkload(member.getWorkload());
        }
      }

      // サーバーに残すファイルのID
      List<Integer> attIdList = pfile.getRequestedHasFileIdList(fileuploadList);
      // 現在選択しているエントリが持っているファイル
      List<EipTProjectTaskFile> files =
        pfile.getSelectQueryForFiles(
          EipTProjectTask.TASK_ID_PK_COLUMN,
          task.getTaskId()).fetchList();
      if (files != null) {
        for (int i = 0; i < files.size(); i++) {
          EipTProjectTaskFile file = files.get(i);
          if (!attIdList.contains(file.getFileId())) {
            // ファイルシステムから削除
            ALStorageService.deleteFile(ProjectFile.getSaveDirPath(uid)
              + file.getFilePath());

            // DBから削除
            Database.delete(file);

          }
        }
      }

      // 添付ファイルを登録する．
      pfile.insertAttachmentFiles(
        fileuploadList,
        folderName,
        uid,
        task,
        msgList);

      // タスクを更新
      Database.commit();

      // 親タスク情報を更新
      updateParentTask(task.getParentTaskId());
      Database.commit();

      // プロジェクト情報を更新
      if (updateProject(task.getProjectId())) {
        Database.commit();
      }

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        task.getTaskId(),
        ALEventlogConstants.PORTLET_TYPE_NONE,
        "プロジェクト管理タスク 「" + task.getTaskName() + "」 更新");

    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * タスクをデータベースから削除します。
   * 
   * @param rundata
   *          RunData
   * @param context
   *          Context
   * @param msgList
   *          エラーメッセージリスト
   * @return TRUE 成功 FALSE 失敗
   */
  @Override
  protected boolean deleteFormData(RunData rundata, Context context,
      List<String> msgList) {
    try {
      // オブジェクトモデルを取得
      EipTProjectTask task = ProjectUtils.getEipTProjectTask(rundata, context);
      if (task == null) {
        return false;
      }

      if (hasChildren) {
        msgList.add("子タスクが存在するため削除できません。");
        return false;
      }

      // タスクに紐づく担当者を削除
      ProjectUtils.removeProjectTaskMember(task);

      // タスクに紐づくコメントを削除
      ProjectUtils.removeProjectTaskComment(rundata, task);

      // タスクに紐づくファイルを削除
      pfile.removeFile(
        rundata,
        EipTProjectTaskFile.EIP_TPROJECT_TASK_PROPERTY,
        task.getTaskId());

      // タスクを削除
      Database.delete(task);
      Database.commit();

      // イベントログに保存
      ALEventlogFactoryService.getInstance().getEventlogHandler().log(
        task.getTaskId(),
        ALEventlogConstants.PORTLET_TYPE_NONE,
        "プロジェクト管理タスク 「" + task.getTaskName() + "」 削除");

    } catch (Exception ex) {
      Database.rollback();
      logger.error("Exception", ex);
      return false;
    }
    return true;
  }

  /**
   * 分類を取得します。
   * 
   * @return 分類
   */
  public ALStringField getTracker() {
    return tracker;
  }

  /**
   * タスク名を取得します。
   * 
   * @return タスク名
   */
  public ALStringField getTaskName() {
    return task_name;
  }

  /**
   * 説明を取得します。
   * 
   * @return 説明
   */
  public ALStringField getExplanation() {
    return explanation;
  }

  /**
   * ステータスを取得します。
   * 
   * @return ステータス
   */
  public ALStringField getStatus() {
    return status;
  }

  /**
   * 優先度を取得します。
   * 
   * @return 優先度
   */
  public ALStringField getPriority() {
    return priority;
  }

  /**
   * 開始予定日を取得します。
   * 
   * @return 開始予定日
   */
  public ALDateField getStartPlanDate() {
    return start_plan_date;
  }

  /**
   * 完了予定日を取得します。
   * 
   * @return 完了予定日
   */
  public ALDateField getEndPlanDate() {
    return end_plan_date;
  }

  /**
   * 開始実績日を取得します。
   * 
   * @return 開始実績日
   */
  public ALDateField getStartDate() {
    return start_date;
  }

  /**
   * 完了実績日を取得します。
   * 
   * @return 完了実績日
   */
  public ALDateField getEndDate() {
    return end_date;
  }

  /**
   * 開始予定日を取得します。（画面表示用）
   * 
   * @return 開始予定日
   */
  public String getStartPlanDateString() {
    try {
      return ALDateUtil.format(
        start_plan_date.getValue().getDate(),
        ProjectUtils.DISP_DATE_FORMAT);
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * 完了予定日を取得します。（画面表示用）
   * 
   * @return 完了予定日
   */
  public String getEndPlanDateString() {
    try {
      return ALDateUtil.format(
        end_plan_date.getValue().getDate(),
        ProjectUtils.DISP_DATE_FORMAT);
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * 開始実績日を取得します。（画面表示用）
   * 
   * @return 開始予定日
   */
  public String getStartDateString() {
    try {
      return ALDateUtil.format(
        start_date.getValue().getDate(),
        ProjectUtils.DISP_DATE_FORMAT);
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * 完了実績日を取得します。（画面表示用）
   * 
   * @return 完了予定日
   */
  public String getEndDateString() {
    try {
      return ALDateUtil.format(
        end_date.getValue().getDate(),
        ProjectUtils.DISP_DATE_FORMAT);
    } catch (Exception e) {
      return "";
    }
  }

  /**
   * 開始予定日指定フラグを取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getStartPlanDateCheck() {
    return start_plan_date_check;
  }

  /**
   * 完了予定日指定フラグを取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getEndPlanDateCheck() {
    return end_plan_date_check;
  }

  /**
   * 開始実績日指定フラグを取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getStartDateCheck() {
    return start_date_check;
  }

  /**
   * 完了実績日指定フラグを取得します。 <BR>
   * 
   * @return
   */
  public ALStringField getEndDateCheck() {
    return end_date_check;
  }

  /**
   * 計画工数を取得します。
   * 
   * @return 計画工数
   */
  public BigDecimal getPlanWorkload() {
    return plan_workload;
  }

  /**
   * 進捗率を取得します。
   * 
   * @return 進捗率
   */
  public ALNumberField getProgressRate() {
    return progress_rate;
  }

  /**
   * 部署情報を取得する
   * 
   * @return 部署情報
   */
  public Map<Integer, ALEipPost> getPostMap() {
    return ALEipManager.getInstance().getPostMap();
  }

  /**
   * ログインユーザーIDを返します。
   * 
   * @return ログインユーザーID
   */
  public int getLoginUserId() {
    return (int) loginUser.getUserId().getValue();
  }

  /**
   * 親タスクIDを取得します。
   * 
   * @return 親タスクID
   */
  public Integer getParentTaskId() {
    return parentTaskId;
  }

  /**
   * 子タスク保持フラグを取得します。
   * 
   * @return 子タスク保持フラグ
   */
  public boolean getHasChildren() {
    return hasChildren;
  }

  /**
   * 分類リストを返します。
   * 
   * @return 分類リスト
   */
  public Map<String, String> getTrackerMap() {
    return ProjectUtils.getKubunMap("tracker");
  }

  /**
   * ステータスリストを返します。
   * 
   * @return ステータスリスト
   */
  public Map<String, String> getStatusMap() {
    return ProjectUtils.getKubunMap("status");
  }

  /**
   * 優先度リストを返します。
   * 
   * @return 優先度リスト
   */
  public Map<String, String> getPriorityMap() {
    return ProjectUtils.getKubunMap("priority");
  }

  /**
   * 担当者リストを返します。
   * 
   * @return 担当者リスト
   */
  public List<ProjectTaskMemberResultData> getTaskMembers() {
    return taskMembers;
  }

  /**
   * プロジェクトメンバーリストを返します。
   * 
   * @return プロジェクトメンバーリスト
   */
  public List<ALEipUser> getProjectMembers() {
    return projectMembers;
  }

  /**
   * アップロードファイルリストを返します。
   * 
   * @return アップロードファイルリスト
   */
  public List<FileuploadBean> getAttachmentFileNameList() {
    return fileuploadList;
  }
}