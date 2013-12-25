CREATE TABLE "ia_form_item" ("_id" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE, "itemId" INTEGER NOT NULL, "formId" INTEGER NOT NULL, "formTypeId" INTEGER NOT NULL, "enterpriseId" TEXT NOT NULL, "itemName" VARCHAR(40) NOT NULL, "itemPhysicalName" VARCHAR(40) NOT NULL, "itemType" INTEGER NOT NULL DEFAULT 0, "isMustWrite" BOOLEAN NOT NULL DEFAULT 0, "isCapital" BOOLEAN NOT NULL DEFAULT 0, "formula" VARCHAR(120))