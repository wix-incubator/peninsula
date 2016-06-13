package com.wix.peninsula

import com.wix.peninsula.JsonValidators.DefaultFieldValidator

trait CopyConfig

case class MergeObjectConfig(from: String) extends CopyConfig

case class CopyArrayConfig(from: String,
                           to: String,
                           config: TransformationConfig,
                           idField: Option[String] = None) extends CopyConfig

case class CopyFieldConfigCollection(configs: Seq[CopyFieldConfig]) {
  def withValidators(validators: JsonValidator*): CopyFieldConfigCollection =
    copy(configs = configs.map(_.withValidators(validators: _*)))
}

case class CopyFieldConfig(from: String,
                           to: String,
                           mapper: JsonMapper = IdentityMapper,
                           validators: Seq[JsonValidator] = Seq(DefaultFieldValidator)) extends CopyConfig {
  def withMapper(mapper: JsonMapper): CopyFieldConfig = copy(mapper = mapper)
  def withValidators(validators: JsonValidator*): CopyFieldConfig =
    this.copy(validators = this.validators ++ validators)
}
