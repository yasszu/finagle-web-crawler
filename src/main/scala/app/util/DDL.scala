package app.util

object DDL {

  val createSchema =
    """CREATE SCHEMA IF NOT EXISTS `finagle_web_crawler` DEFAULT CHARACTER SET utf8mb4"""

  val createArticlesTable =
    """CREATE TABLE IF NOT EXISTS `articles` (
         `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
         `published` VARCHAR(45) NOT NULL,
         `title` TEXT NOT NULL,
         `content` TEXT NOT NULL,
         `thumbnail` VARCHAR(255) NOT NULL,
         `link` VARCHAR(255) NOT NULL,
         `organization_id` BIGINT(20) NOT NULL,
         `del_flg` tinyint(4) NOT NULL DEFAULT '0',
         `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
         `update_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
         PRIMARY KEY (`id`),
         UNIQUE INDEX `id_UNIQUE` (`id` ASC),
         INDEX `organization_id_idx` (`organization_id` ASC))
       ENGINE = InnoDB"""

  val createCategoriesTable =
    """CREATE TABLE IF NOT EXISTS `categories` (
         `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
         `term` VARCHAR(255) NOT NULL,
         `article_id` BIGINT(20) NOT NULL,
         `del_flg` tinyint(4) NOT NULL DEFAULT '0',
         `create_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
         `update_date` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
         PRIMARY KEY (`id`),
         UNIQUE INDEX `id_UNIQUE` (`id` ASC),
         INDEX `article_id_idx` (`article_id` ASC))
       ENGINE = InnoDB"""

}
