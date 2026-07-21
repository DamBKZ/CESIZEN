-- liquibase formatted sql
-- changeset damien:014_seed_categories

INSERT INTO category (
    categoryID,
    categoryName,
    categoryDescription
)
SELECT
    '72a158a9-4142-4c84-80f8-3f757d6cf605',
    'Stress',
    'Informations relatives au stress et Ã  sa gestion'
WHERE NOT EXISTS (
    SELECT 1
    FROM category
    WHERE categoryName = 'Stress'
);

INSERT INTO category (
    categoryID,
    categoryName,
    categoryDescription
)
SELECT
    '9f632fbe-cdf5-4664-a5ca-2744a57af820',
    'Relaxation',
    'Techniques et pratiques de relaxation'
WHERE NOT EXISTS (
    SELECT 1
    FROM category
    WHERE categoryName = 'Relaxation'
);

INSERT INTO category (
    categoryID,
    categoryName,
    categoryDescription
)
SELECT
    '401de6fd-eccb-48cd-8da5-d0e768e68dd7',
    'Bien-Ãªtre',
    'Conseils et ressources autour du bien-Ãªtre'
WHERE NOT EXISTS (
    SELECT 1
    FROM category
    WHERE categoryName = 'Bien-Ãªtre'
);

INSERT INTO category (
    categoryID,
    categoryName,
    categoryDescription
)
SELECT
    '52f2797a-de4a-480c-81fb-7a4983b15940',
    'Sommeil',
    'Informations consacrÃ©es au sommeil'
WHERE NOT EXISTS (
    SELECT 1
    FROM category
    WHERE categoryName = 'Sommeil'
);

INSERT INTO category (
    categoryID,
    categoryName,
    categoryDescription
)
SELECT
    'eed83571-05cc-44bb-a562-5984e594fbc5',
    'Travail',
    'Stress, Ã©quilibre et bien-Ãªtre dans le cadre professionnel'
WHERE NOT EXISTS (
    SELECT 1
    FROM category
    WHERE categoryName = 'Travail'
);

INSERT INTO category (
    categoryID,
    categoryName,
    categoryDescription
)
SELECT
    'ac62b748-0e37-4cf4-8f6c-b2528ff33864',
    'PrÃ©vention',
    'Conseils et ressources de prÃ©vention'
WHERE NOT EXISTS (
    SELECT 1
    FROM category
    WHERE categoryName = 'PrÃ©vention'
);

-- rollback DELETE FROM category WHERE categoryID IN ('72a158a9-4142-4c84-80f8-3f757d6cf605', '9f632fbe-cdf5-4664-a5ca-2744a57af820', '401de6fd-eccb-48cd-8da5-d0e768e68dd7', '52f2797a-de4a-480c-81fb-7a4983b15940', 'eed83571-05cc-44bb-a562-5984e594fbc5', 'ac62b748-0e37-4cf4-8f6c-b2528ff33864');
