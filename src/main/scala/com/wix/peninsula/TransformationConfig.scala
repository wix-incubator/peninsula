package com.wix.peninsula

case class TransformationConfig(copyConfigs: Seq[CopyConfig] = Seq()) {
  def add(configSeq: CopyFieldConfigCollection): TransformationConfig = TransformationConfig(copyConfigs ++ configSeq.configs)
  def add(config: CopyConfig): TransformationConfig = TransformationConfig(config +: copyConfigs)
}

object CopyConfigFactory {

  def copy(fromTo: String): CopyFieldConfig = {
    CopyFieldConfig(fromTo, fromTo, validators = Seq())
  }

  def copyField(fromTo: String): CopyFieldConfig = {
    CopyFieldConfig(fromTo, fromTo)
  }

  def copyField(fromTo: (String, String)): CopyFieldConfig = {
    CopyFieldConfig(fromTo._1, fromTo._2)
  }

  def mergeObject(from: String): MergeObjectConfig = {
    MergeObjectConfig(from)
  }

  def copyArrayOfPrimitives(fromTo: (String, String)): CopyFieldConfig = {
    CopyFieldConfig(fromTo._1, fromTo._2, validators = Seq())
  }

  def copyArrayOfPrimitives(fromTo: String): CopyFieldConfig = {
    CopyFieldConfig(fromTo, fromTo, validators = Seq())
  }

  def copyArrayOfObjects(fromTo: String, config: TransformationConfig, idField: Option[String] = None): CopyArrayConfig = {
    CopyArrayConfig(fromTo, fromTo, config, idField)
  }

  def copyArrayOfObjects(fromTo: (String, String), config: TransformationConfig): CopyArrayConfig = {
    CopyArrayConfig(fromTo._1, fromTo._2, config, None)
  }

  def copyArrayOfObjects(fromTo: (String, String), config: TransformationConfig, idField: String): CopyArrayConfig = {
    CopyArrayConfig(fromTo._1, fromTo._2, config, Some(idField))
  }

  def copyArrayOfObjects(fromTo: String, config: TransformationConfig, idField: String): CopyArrayConfig = {
    CopyArrayConfig(fromTo, fromTo, config, Some(idField))
  }

  def copyFields(fields: String*): CopyFieldConfigCollection =
    CopyFieldConfigCollection(fields.map { f => CopyFieldConfig(f, f) })

  def copyFieldsFromTo(fromTos: (String, String)*): CopyFieldConfigCollection =
    CopyFieldConfigCollection(fromTos.map { f => CopyFieldConfig(f._1, f._2) })

}
