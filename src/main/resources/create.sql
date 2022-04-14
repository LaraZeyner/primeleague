USE trues;

CREATE TABLE `season`
(
    season_id    SMALLINT(5) UNSIGNED PRIMARY KEY,
    season_name  VARCHAR(17) NOT NULL UNIQUE,
    season_start DATE        NOT NULL UNIQUE,
    season_end   DATE        NOT NULL UNIQUE,
    CHECK ( season_start < season_end )
);

CREATE TABLE `stage`
(
    stage_id    TINYINT(2) AUTO_INCREMENT PRIMARY KEY,
    season      SMALLINT(5) UNSIGNED NOT NULL,
    stage_type  VARCHAR(8)           NOT NULL,
    stage_start DATE                 NOT NULL UNIQUE,
    stage_end   DATE                 NOT NULL UNIQUE,
    UNIQUE INDEX idx_stage_type (season ASC, stage_type ASC),
    FOREIGN KEY (season) REFERENCES `season` (season_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CHECK ( stage_start < stage_end )
);

CREATE TABLE `league`
(
    league_id   SMALLINT(5) UNSIGNED PRIMARY KEY,
    stage       TINYINT(2)  NOT NULL,
    league_name VARCHAR(13) NOT NULL,
    FOREIGN KEY (stage) REFERENCES `stage` (stage_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE INDEX idx_league_name (stage ASC, league_name ASC)
);

CREATE TABLE `matchday`
(
    matchday_id    SMALLINT(4) AUTO_INCREMENT PRIMARY KEY,
    stage          TINYINT(2)  NOT NULL,
    matchday_type  VARCHAR(11) NOT NULL,
    matchday_start TIMESTAMP   NOT NULL UNIQUE,
    matchday_end   TIMESTAMP   NOT NULL UNIQUE,
    FOREIGN KEY (stage) REFERENCES `stage` (stage_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE INDEX idx_matchday (stage ASC, matchday_type ASC),
    CHECK ( matchday_start < matchday_end )
);

CREATE TABLE `team`
(
    team_id     SMALLINT(4) AUTO_INCREMENT PRIMARY KEY,
    team_tId    INTEGER(6) UNSIGNED  NULL UNIQUE,
    team_name   VARCHAR(100)         NOT NULL UNIQUE,
    team_abbr   VARCHAR(10)          NOT NULL UNIQUE,
    league      SMALLINT(5) UNSIGNED NULL,
    team_result VARCHAR(30)          NULL,
    scrims      BOOLEAN              NULL,
    FOREIGN KEY (league) REFERENCES `league` (league_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE `resource`
(
    resource_name VARCHAR(12) PRIMARY KEY
);

CREATE TABLE `champion`
(
    champion_id    SMALLINT(4) PRIMARY KEY,
    champion_name  VARCHAR(16)            NOT NULL UNIQUE,
    champion_title VARCHAR(30)            NOT NULL UNIQUE,
    subclass       VARCHAR(14)            NULL,
    resource       VARCHAR(16)            NOT NULL,
    attack         TINYINT(2) UNSIGNED    NOT NULL,
    defense        TINYINT(2) UNSIGNED    NOT NULL,
    spell          TINYINT(2) UNSIGNED    NOT NULL,
    health         SMALLINT(4) UNSIGNED   NOT NULL,
    secondary      SMALLINT(5) UNSIGNED   NOT NULL,
    move_speed     SMALLINT(3) UNSIGNED   NOT NULL,
    resist         DECIMAL(9, 6) UNSIGNED NOT NULL,
    attack_range   SMALLINT(4) UNSIGNED   NOT NULL,
    health_regen   DECIMAL(9, 6) UNSIGNED NOT NULL,
    spell_regen    DECIMAL(9, 6) UNSIGNED NOT NULL,
    damage         TINYINT(3) UNSIGNED    NOT NULL,
    attack_speed   DECIMAL(9, 8) UNSIGNED NOT NULL,
    fight_type     VARCHAR(9)             NULL,
    fight_style    VARCHAR(4)             NULL,
    FOREIGN KEY (resource) REFERENCES `resource` (resource_name)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `turnament_match`
(
    match_id    INTEGER(7) UNSIGNED PRIMARY KEY,
    league      SMALLINT(5) UNSIGNED NOT NULL,
    matchday    SMALLINT(4)          NOT NULL,
    match_start TIMESTAMP            NOT NULL,
    home_team   SMALLINT(4)          NULL,
    guest_team  SMALLINT(4)          NULL,
    score       VARCHAR(3)           NOT NULL DEFAULT '-:-' CHECK ( score REGEXP ('^[:digit:]+:[:digit:]+$') OR score = '-:-'),
    matchstate  VARCHAR(17)          NOT NULL DEFAULT 'CREATED',
    FOREIGN KEY (league) REFERENCES `league` (league_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (matchday) REFERENCES `matchday` (matchday_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (home_team) REFERENCES `team` (team_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (guest_team) REFERENCES `team` (team_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE `map`
(
    map_name VARCHAR(20) PRIMARY KEY

);

CREATE TABLE `gametype`
(
    gametype_id   SMALLINT(4) PRIMARY KEY,
    gametype_name VARCHAR(50) NOT NULL,
    map           VARCHAR(20) NOT NULL,
    UNIQUE INDEX idx_gametype (gametype_name ASC, map ASC),
    FOREIGN KEY (map) REFERENCES `map` (map_name)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE `scheduledgame`
(
    game_id   VARCHAR(16) PRIMARY KEY CHECK ( game_id REGEXP ('^EUW') ),
    queuetype VARCHAR(7) NOT NULL
);

CREATE TABLE `game`
(
    game_id        VARCHAR(16) PRIMARY KEY,
    turnamentmatch INTEGER(7) UNSIGNED  NULL,
    game_start     TIMESTAMP            NOT NULL,
    duration       SMALLINT(4) UNSIGNED NOT NULL CHECK ( duration > 300 ),
    gametype       SMALLINT(4)          NOT NULL,
    FOREIGN KEY (turnamentmatch) REFERENCES `turnament_match` (match_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE `abilitystyle`
(
    style_name VARCHAR(30) PRIMARY KEY
);

CREATE TABLE `ability`
(
    ability_id    SMALLINT(4) AUTO_INCREMENT PRIMARY KEY,
    champion      SMALLINT(4) NOT NULL,
    ability_type  VARCHAR(9)  NOT NULL,
    cooldown      VARCHAR(24) NULL,
    resource_cost VARCHAR(24) NULL,
    ability_range VARCHAR(24) NULL,
    value         VARCHAR(29) NULL,
    FOREIGN KEY (champion) REFERENCES `champion` (champion_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    UNIQUE INDEX uq_ability (champion ASC, ability_type ASC)
);

CREATE TABLE `ability_style`
(
    ability      SMALLINT(4) NOT NULL,
    abilitystyle VARCHAR(30) NOT NULL,
    PRIMARY KEY (ability, abilitystyle),
    FOREIGN KEY (ability) REFERENCES `ability` (ability_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (abilitystyle) REFERENCES `abilitystyle` (style_name)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `playstyle`
(
    champion   SMALLINT(4) NOT NULL,
    game_phase VARCHAR(12) NOT NULL,
    playstyle  VARCHAR(18) NOT NULL,
    PRIMARY KEY (champion, game_phase),
    FOREIGN KEY (champion) REFERENCES `champion` (champion_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `champion_relationship`
(
    relationship_type VARCHAR(9)  NOT NULL,
    from_champion     SMALLINT(4) NOT NULL,
    to_champion       SMALLINT(4) NOT NULL,
    PRIMARY KEY (relationship_type, from_champion, to_champion),
    FOREIGN KEY (from_champion) REFERENCES `champion` (champion_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (to_champion) REFERENCES `champion` (champion_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `wincondition`
(
    champion            SMALLINT(4)      NOT NULL,
    wincondition_type   VARCHAR(18)      NOT NULL,
    wincondition_amount TINYINT UNSIGNED NOT NULL CHECK ( wincondition_amount < 31 ),
    PRIMARY KEY (champion, wincondition_type),
    FOREIGN KEY (champion) REFERENCES `champion` (champion_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `champion_selection`
(
    game            VARCHAR(16)      NOT NULL,
    selection_type  VARCHAR(4)       NOT NULL,
    selection_order TINYINT UNSIGNED NOT NULL CHECK ( selection_order < 11 ),
    champion        SMALLINT(4)      NOT NULL,
    PRIMARY KEY (game, selection_type, selection_order),
    FOREIGN KEY (game) REFERENCES `game` (game_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (champion) REFERENCES `champion` (champion_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `teamperformance`
(
    teamperformance_id INTEGER(7) AUTO_INCREMENT PRIMARY KEY,
    game               VARCHAR(16)          NOT NULL,
    team               SMALLINT(4)          NULL,                             -- not null = team round (custom, clash as 3+ or others as 5)
    first_pick         BOOLEAN              NOT NULL,
    win                BOOLEAN              NOT NULL,
    total_damage       INTEGER(6) UNSIGNED  NOT NULL,
    total_damage_taken INTEGER(6) UNSIGNED  NOT NULL,
    total_gold         INTEGER(6) UNSIGNED  NOT NULL,
    total_cs           SMALLINT(4) UNSIGNED NOT NULL,
    total_kills        TINYINT(3) UNSIGNED  NOT NULL,
    towers             TINYINT(2) UNSIGNED  NOT NULL CHECK ( towers <= 11 ),
    drakes             TINYINT(2) UNSIGNED  NOT NULL,
    inhibs             TINYINT(2) UNSIGNED  NOT NULL,
    heralds            TINYINT(1) UNSIGNED  NOT NULL CHECK ( heralds <= 2 ),
    barons             TINYINT(2) UNSIGNED  NOT NULL,
    first_tower        BOOLEAN              NOT NULL,
    first_drake        BOOLEAN              NOT NULL,
    perfect_soul       BOOLEAN              NULL,
    rift_turrets       DECIMAL(2, 1)        NULL CHECK ( rift_turrets <= 5 ), -- divided with 10
    elder_time         SMALLINT(4) UNSIGNED NULL,
    baron_powerplay    SMALLINT(4) UNSIGNED NULL,
    surrender          BOOLEAN              NOT NULL,
    ace_before_15      TINYINT(2)           NULL,
    baron_time         SMALLINT(4)          NULL,
    dragon_time        SMALLINT(4)          NULL,
    objective_onspawn  TINYINT(2)           NULL,
    objective_contest  TINYINT(2)           NULL,
    support_quest      BOOLEAN              NULL,
    inhibitors_time    SMALLINT(4)          NULL,
    ace_flawless       TINYINT(1) UNSIGNED  NULL,
    rift_multiturret   TINYINT(1) UNSIGNED  NULL,
    ace_fastest        SMALLINT(4) UNSIGNED NULL,
    kills_deficit      TINYINT(3) UNSIGNED  NULL,
    dragon_soul        VARCHAR(8)           NULL,
    FOREIGN KEY (game) REFERENCES `game` (game_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (team) REFERENCES `team` (team_id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    UNIQUE INDEX idx_teamperformance_side (game ASC, first_pick DESC),
    UNIQUE INDEX idx_teamperformance_win (game ASC, win DESC)
);

CREATE TABLE `player`
(
    player_id   INTEGER(7) UNSIGNED PRIMARY KEY,
    player_name VARCHAR(100) NOT NULL UNIQUE,
    team        SMALLINT(4)  NULL,
    player_role VARCHAR(7)   NULL,
    FOREIGN KEY (team) REFERENCES `team` (team_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE `matchlog`
(
    log_id         INTEGER(7) UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    turnamentmatch INTEGER(7) UNSIGNED NOT NULL,
    log_time       TIMESTAMP           NOT NULL,
    player         INTEGER(7) UNSIGNED NULL,
    team           SMALLINT(4)         NULL,
    log_action     VARCHAR(14)         NOT NULL,
    log_details    VARCHAR(250)        NOT NULL,
    UNIQUE INDEX idx_matchlog (turnamentmatch ASC, log_time ASC),
    FOREIGN KEY (turnamentmatch) REFERENCES `turnament_match` (match_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (player) REFERENCES `player` (player_id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (team) REFERENCES `team` (team_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE `account`
(
    puuid          VARCHAR(78) PRIMARY KEY,
    account_id     VARCHAR(47)          NOT NULL UNIQUE,
    account_name   VARCHAR(16)          NOT NULL UNIQUE,
    player         INTEGER(7) UNSIGNED  NULL,
    icon           SMALLINT(5)          NOT NULL,
    account_level  SMALLINT(4) UNSIGNED NOT NULL,
    account_active BOOLEAN              NOT NULL,
    last_update    TIMESTAMP            NOT NULL,
    FOREIGN KEY (player) REFERENCES `player` (player_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE `season_elo`
(
    account VARCHAR(78)          NOT NULL,
    season  SMALLINT(5) UNSIGNED NOT NULL,
    mmr     SMALLINT(4) UNSIGNED NOT NULL DEFAULT 25,
    wins    SMALLINT(4) UNSIGNED NOT NULL,
    losses  SMALLINT(4) UNSIGNED NOT NULL,
    PRIMARY KEY (account, season),
    FOREIGN KEY (account) REFERENCES `account` (puuid)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (season) REFERENCES `season` (season_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `runetree`
(
    runetree_id   SMALLINT(4) UNSIGNED PRIMARY KEY,
    runetree_name VARCHAR(11) NOT NULL,
    runetree_icon VARCHAR(38) NOT NULL
);

CREATE TABLE `rune`
(
    rune_id          SMALLINT(4) UNSIGNED PRIMARY KEY,
    runetree         SMALLINT(4) UNSIGNED,
    rune_slot        TINYINT(2) UNSIGNED,
    rune_name        VARCHAR(30)  NOT NULL UNIQUE,
    rune_description VARCHAR(750) NOT NULL,
    rune_short       VARCHAR(750) NOT NULL,
    UNIQUE INDEX idx_rune (runetree ASC, rune_slot ASC),
    FOREIGN KEY (runetree) REFERENCES `runetree` (runetree_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `itemstat`
(
    itemstat_id   VARCHAR(35) PRIMARY KEY,
    itemstat_name VARCHAR(35) NULL UNIQUE
);

CREATE TABLE `itemstyle`
(
    style_name VARCHAR(25) PRIMARY KEY
);

CREATE TABLE `item`
(
    item_id           SMALLINT(4) UNSIGNED PRIMARY KEY,
    itemtype          VARCHAR(10)   NOT NULL,
    item_name         VARCHAR(50)   NOT NULL UNIQUE,
    item_description  VARCHAR(1250) NOT NULL,
    short_description VARCHAR(250)  NULL,
    cost              SMALLINT(4)   NOT NULL
);

CREATE TABLE `item_stat`
(
    item        SMALLINT(4) UNSIGNED NOT NULL,
    stat        VARCHAR(35)          NOT NULL,
    stat_amount DECIMAL(9, 4)        NOT NULL,
    PRIMARY KEY (item, stat),
    FOREIGN KEY (item) REFERENCES `item` (item_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (stat) REFERENCES `itemstat` (itemstat_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);



CREATE TABLE `item_style`
(
    item      SMALLINT(4) UNSIGNED NOT NULL,
    itemstyle VARCHAR(25)          NOT NULL,
    PRIMARY KEY (item, itemstyle),
    FOREIGN KEY (item) REFERENCES `item` (item_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (itemstyle) REFERENCES `itemstyle` (style_name)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `playerperformance`
(
    playerperformance_id   INTEGER(8) AUTO_INCREMENT PRIMARY KEY,
    teamperformance        INTEGER(6)           NOT NULL,
    account                VARCHAR(78)          NOT NULL,
    lane                   VARCHAR(7)           NOT NULL,
    champion_own           SMALLINT(4)          NOT NULL,
    champion_enemy         SMALLINT(4)          NULL,
    q_usages               SMALLINT(4) UNSIGNED NOT NULL, -- 1301
    w_usages               SMALLINT(4) UNSIGNED NOT NULL, -- 545
    e_usages               SMALLINT(4) UNSIGNED NOT NULL, -- 815
    r_usages               SMALLINT(3) UNSIGNED NOT NULL, -- 585
    spells_hit             SMALLINT(4) UNSIGNED NULL,     -- 652
    spells_dodged          SMALLINT(4) UNSIGNED NULL,     -- 995
    quick_dodged           SMALLINT(4) UNSIGNED NULL,     -- 796
    damage_magical         INTEGER(6) UNSIGNED  NOT NULL, -- 134629
    damage_physical        INTEGER(6) UNSIGNED  NOT NULL, -- 95709
    damage_total           INTEGER(6) UNSIGNED  NOT NULL, -- 137957
    damage_taken           INTEGER(6) UNSIGNED  NOT NULL, -- 134720
    damage_mitigated       INTEGER(6) UNSIGNED  NOT NULL,
    damage_healed          INTEGER(6) UNSIGNED  NOT NULL,
    damage_shielded        INTEGER(6) UNSIGNED  NOT NULL,
    kills                  TINYINT(3) UNSIGNED  NOT NULL,
    deaths                 TINYINT(3) UNSIGNED  NOT NULL,
    assists                TINYINT(3) UNSIGNED  NOT NULL,
    kills_solo             TINYINT(2) UNSIGNED  NULL,
    allin_levelup          TINYINT(1) UNSIGNED  NULL CHECK ( allin_levelup < 18 ),
    kills_multi_double     TINYINT(2) UNSIGNED  NOT NULL,
    kills_multi_triple     TINYINT(2) UNSIGNED  NOT NULL,
    kills_multi_quadra     TINYINT(2) UNSIGNED  NOT NULL,
    kills_multi_penta      TINYINT(2) UNSIGNED  NOT NULL,
    flash_aggressive       TINYINT(1) UNSIGNED  NULL,
    time_alive             SMALLINT(4) UNSIGNED NOT NULL,
    time_dead              SMALLINT(4) UNSIGNED NOT NULL, -- Percentage calc
    kills_teleport         TINYINT(2) UNSIGNED  NULL,
    immobilizations        TINYINT(3) UNSIGNED  NULL,
    wards_control          TINYINT(2) UNSIGNED  NOT NULL,
    wards_control_coverage TINYINT(3) UNSIGNED  NULL,
    wards_placed           TINYINT(3) UNSIGNED  NOT NULL,
    wards_cleared          TINYINT(3) UNSIGNED  NOT NULL,
    wards_guarded          TINYINT(2) UNSIGNED  NULL,
    visionscore            TINYINT(3) UNSIGNED  NOT NULL,
    visionscore_advantage  TINYINT(3)           NULL,
    objectives_stolen      TINYINT(1) UNSIGNED  NOT NULL,
    firstturret_advantage  SMALLINT(4)          NULL,
    objectives_damage      INTEGER(6) UNSIGNED  NOT NULL,
    baron_executes         TINYINT(1) UNSIGNED  NULL,
    baron_kills            TINYINT(1) UNSIGNED  NOT NULL,
    buffs_stolen           TINYINT(2) UNSIGNED  NULL,
    scuttles_initial       TINYINT(1) UNSIGNED  NULL CHECK ( scuttles_initial <= 2),
    scuttles_total         TINYINT(2) UNSIGNED  NULL,
    turrets_splitpushed    TINYINT(2) UNSIGNED  NULL CHECK ( turrets_splitpushed <= 11 ),
    team_invading          TINYINT(2) UNSIGNED  NULL,
    ganks_early            TINYINT(2) UNSIGNED  NULL,
    ganks_total            TINYINT(2) UNSIGNED  NOT NULL,
    ganks_top              TINYINT(2) UNSIGNED  NOT NULL,
    ganks_mid              TINYINT(2) UNSIGNED  NOT NULL,
    ganks_bot              TINYINT(2) UNSIGNED  NOT NULL,
    dives_done             TINYINT(2) UNSIGNED  NULL,
    dives_successful       TINYINT(2) UNSIGNED  NULL,
    dives_gotten           TINYINT(2) UNSIGNED  NULL,
    dives_protected        TINYINT(2) UNSIGNED  NULL,
    gold_total             SMALLINT(5) UNSIGNED NOT NULL,
    gold_bounty            SMALLINT(4) UNSIGNED NULL,
    experience_total       SMALLINT(5) UNSIGNED NOT NULL,
    creeps_total           SMALLINT(4) UNSIGNED NOT NULL,
    creeps_early           TINYINT(3) UNSIGNED  NULL,
    creeps_invade          TINYINT(3) UNSIGNED  NOT NULL,
    early_lane_lead        SMALLINT(5)          NULL,
    lane_lead              SMALLINT(5)          NULL,
    turretplates           TINYINT(2) UNSIGNED  NULL CHECK ( turretplates <= 15 ),
    flamehorizon_advantage SMALLINT(3)          NULL,
    items_amount           TINYINT(3) UNSIGNED  NOT NULL,
    mejais_completed       SMALLINT(4) UNSIGNED NULL,
    first_blood            BOOLEAN              NOT NULL,
    outplayed_opponent     TINYINT(2) UNSIGNED  NULL,
    turret_takedowns       TINYINT(2) UNSIGNED  NOT NULL,
    dragon_takedowns       TINYINT(1) UNSIGNED  NULL,
    fastest_legendary      SMALLINT(4) UNSIGNED NULL,
    gank_setups            TINYINT(1) UNSIGNED  NULL,
    buffs_initial          TINYINT(1) UNSIGNED  NULL CHECK ( buffs_initial BETWEEN 0 AND 2 ),
    kills_early            TINYINT(2) UNSIGNED  NULL,
    objective_junglerkill  TINYINT(1) UNSIGNED  NULL,
    ambush_kill            TINYINT(2) UNSIGNED  NULL,
    turrets_early          TINYINT(1) UNSIGNED  NULL,
    experience_advantage   TINYINT(1) UNSIGNED  NULL,
    pick_kill              TINYINT(2) UNSIGNED  NULL,
    assassination          TINYINT(2) UNSIGNED  NULL,
    guard_ally             TINYINT(2) UNSIGNED  NULL,
    survived_close         TINYINT(2) UNSIGNED  NULL,
    FOREIGN KEY (teamperformance) REFERENCES `teamperformance` (teamperformance_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (account) REFERENCES `account` (puuid)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (champion_own) REFERENCES `champion` (champion_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (champion_enemy) REFERENCES `champion` (champion_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    UNIQUE INDEX idx_playerperformance_player (teamperformance ASC, account ASC),
    UNIQUE INDEX idx_playerperformance_lane (teamperformance ASC, lane ASC),
    CHECK ( kills_multi_double <= kills / 2 ),
    CHECK ( kills_multi_triple <= kills / 3 ),
    CHECK ( kills_multi_quadra <= kills / 4 ),
    CHECK ( kills_multi_penta <= kills / 5 ),
    CHECK ( scuttles_total >= scuttles_initial ),
    CHECK ( kills_solo <= kills )
);

CREATE TABLE `playerperformance_item`
(
    playerperformance_item_id INTEGER(7) UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    playerperformance         INTEGER(7)           NOT NULL,
    buy_timestamp             SMALLINT(4) UNSIGNED NOT NULL,
    item                      SMALLINT(4) UNSIGNED NOT NULL,
    item_remains              BOOLEAN              NOT NULL,
    FOREIGN KEY (playerperformance) REFERENCES `playerperformance` (playerperformance_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (item) REFERENCES `item` (item_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE `playerperformance_rune`
(
    playerperformance INTEGER(7)           NOT NULL,
    rune              SMALLINT(4) UNSIGNED NOT NULL,
    PRIMARY KEY (playerperformance, rune),
    FOREIGN KEY (playerperformance) REFERENCES `playerperformance` (playerperformance_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (rune) REFERENCES `rune` (rune_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE `summonerspell`
(
    summonerspell_id   TINYINT(2) UNSIGNED PRIMARY KEY,
    summonerspell_name VARCHAR(8) NOT NULL,
    UNIQUE INDEX idx_summonerspell (summonerspell_name)
);

CREATE TABLE `playerperformance_summonerspell`
(
    playerperformance INTEGER(7)          NOT NULL,
    summonerspell     VARCHAR(8)          NOT NULL,
    usages            TINYINT(2) UNSIGNED NOT NULL,
    PRIMARY KEY (playerperformance, summonerspell),
    FOREIGN KEY (playerperformance) REFERENCES `playerperformance` (playerperformance_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (summonerspell) REFERENCES `summonerspell` (summonerspell_name)
        ON DELETE RESTRICT ON UPDATE CASCADE
);



CREATE TABLE `champion_class`
(
    champion      SMALLINT(4) NOT NULL,
    championclass VARCHAR(10) NOT NULL,
    PRIMARY KEY (champion, championclass),
    FOREIGN KEY (champion) REFERENCES `champion` (champion_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `input`
(
    input_category VARCHAR(25) PRIMARY KEY,
    input_value1   VARCHAR(100) NULL,
    input_value2   VARCHAR(100) NULL,
    input_value3   VARCHAR(100) NULL,
    input_value4   VARCHAR(100) NULL,
    input_value5   VARCHAR(100) NULL,
    input_value6   VARCHAR(100) NULL,
    input_value7   VARCHAR(100) NULL,
    input_value8   VARCHAR(100) NULL,
    input_value9   VARCHAR(100) NULL,
    input_value10  VARCHAR(100) NULL,
    input_value11  VARCHAR(100) NULL,
    input_value12  VARCHAR(100) NULL,
    input_value13  VARCHAR(100) NULL
);

CREATE TABLE `schedule`
(
    schedule_id  SMALLINT(4) PRIMARY KEY AUTO_INCREMENT,
    type         VARCHAR(20)  NOT NULL,
    start_time   TIMESTAMP    NOT NULL,
    end_time     TIMESTAMP    NULL,
    enemy_team   SMALLINT(4)  NULL,
    title        VARCHAR(100) NOT NULL,
    small_title  VARCHAR(20)  NULL,
    participants VARCHAR(100) NULL,
    CHECK ( LENGTH(title) <= 20 AND small_title IS NULL OR small_title IS NOT NULL),
    FOREIGN KEY (enemy_team) REFERENCES `team` (team_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE `game_pause`
(
    pause_id    INTEGER(6) PRIMARY KEY AUTO_INCREMENT,
    game        VARCHAR(16)         NOT NULL,
    pause_start BIGINT(13) UNSIGNED NOT NULL,
    pause_end   BIGINT(13) UNSIGNED NOT NULL,
    FOREIGN KEY (game) REFERENCES `game` (game_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `teamperformance_bounty`
(
    bounty_id       INTEGER(6) PRIMARY KEY AUTO_INCREMENT,
    teamperformance INTEGER(6)          NOT NULL,
    bounty_start    INTEGER(7) UNSIGNED NOT NULL,
    bounty_end      INTEGER(7) UNSIGNED NOT NULL,
    FOREIGN KEY (teamperformance) REFERENCES `teamperformance` (teamperformance_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `playerperformance_level`
(
    playerperformance INTEGER(7)          NOT NULL,
    level_number      TINYINT(2) UNSIGNED NOT NULL,
    levelup_time      INTEGER(7) UNSIGNED NOT NULL,
    PRIMARY KEY (playerperformance, level_number),
    FOREIGN KEY (playerperformance) REFERENCES `playerperformance` (playerperformance_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `playerperformance_objective`
(
    objective_id      INTEGER(8) PRIMARY KEY AUTO_INCREMENT,
    playerperformance INTEGER(7)           NOT NULL,
    objective_time    INTEGER(7) UNSIGNED  NOT NULL,
    objective_type    VARCHAR(15)          NOT NULL,
    objective_lane    VARCHAR(6)           NULL,
    objective_bounty  SMALLINT(4) UNSIGNED NOT NULL,
    objective_role    VARCHAR(6)           NOT NULL,
    FOREIGN KEY (playerperformance) REFERENCES `playerperformance` (playerperformance_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `playerperformance_kill`
(
    kill_id           INTEGER(8) UNSIGNED  NOT NULL,
    playerperformance INTEGER(7)           NOT NULL,
    kill_time         INTEGER(7) UNSIGNED  NOT NULL,
    position_x        SMALLINT(5) UNSIGNED NOT NULL,
    position_y        SMALLINT(5) UNSIGNED NOT NULL,
    kill_bounty       SMALLINT(4) UNSIGNED NOT NULL,
    kill_role         VARCHAR(6)           NOT NULL,
    kill_type         VARCHAR(11)          NOT NULL,
    kill_streak       TINYINT(2) UNSIGNED  NOT NULL DEFAULT 0,
    PRIMARY KEY (kill_id, playerperformance),
    FOREIGN KEY (playerperformance) REFERENCES `playerperformance` (playerperformance_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `playerperformance_info`
(
    playerperformance INTEGER(7)           NOT NULL,
    info_minute       TINYINT(3) UNSIGNED  NOT NULL,
    info_gold_total   SMALLINT(5) UNSIGNED NOT NULL,
    info_gold_current SMALLINT(5) UNSIGNED NOT NULL,
    enemy_controlled  SMALLINT(4) UNSIGNED NOT NULL,
    position_x        SMALLINT(5) UNSIGNED NOT NULL,
    position_y        SMALLINT(5) UNSIGNED NOT NULL,
    info_experience   SMALLINT(5) UNSIGNED NOT NULL,
    info_creep_score  SMALLINT(4) UNSIGNED NOT NULL,
    info_damage_total INTEGER(6) UNSIGNED  NOT NULL,
    PRIMARY KEY (playerperformance, info_minute),
    FOREIGN KEY (playerperformance) REFERENCES `playerperformance` (playerperformance_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

INSERT INTO input (input_category)
VALUES ('aktuelles Team');

INSERT INTO input (input_category, input_value1, input_value2, input_value3, input_value4, input_value5, input_value6,
                   input_value7)
VALUES ('Top Championstats', 'Spiele', 'KDA', 'Spiele', 'Winrate', 'Lane lead', 'Early Creep Score', 'Turretplates');

INSERT INTO input (input_category, input_value1, input_value2, input_value3, input_value4, input_value5, input_value6,
                   input_value7)
VALUES ('Jungle Championstats', 'Spiele', 'KDA', 'Spiele', 'Winrate', 'Lane lead', 'Killbeteiligung', 'Invading Farm');

INSERT INTO input (input_category, input_value1, input_value2, input_value3, input_value4, input_value5, input_value6,
                   input_value7)
VALUES ('Middle Championstats', 'Spiele', 'Teamschaden', 'Spiele', 'Winrate', 'Lane lead', 'Spellbilanz', 'XP pro
Minute');

INSERT INTO input (input_category, input_value1, input_value2, input_value3, input_value4, input_value5, input_value6,
                   input_value7)
VALUES ('Bottom Championstats', 'Spiele', 'Teamschaden', 'Spiele', 'Winrate', 'Lane lead', 'Gold pro Minute',
        'Teamschaden');

INSERT INTO input (input_category, input_value1, input_value2, input_value3, input_value4, input_value5, input_value6,
                   input_value7)
VALUES ('Utility Championstats', 'Spiele', 'KDA', 'Spiele', 'Winrate', 'Lane lead', 'Visionscore Lead', 'Roams');

INSERT INTO input (input_category, input_value1, input_value2, input_value3, input_value4, input_value5, input_value6,
                   input_value7, input_value8)
VALUES ('Top Playerstats', 'Lane lead', 'Gold pro Minute', 'Turrets Splitpushing', 'Turretplates', 'Divebilanz',
        'Turret Zeitbilanz', 'Kills durch Teleport', 'Solokills');

INSERT INTO input (input_category, input_value1, input_value2, input_value3, input_value4, input_value5, input_value6,
                   input_value7, input_value8)
VALUES ('Jungle Playerstats', 'Lane lead', 'Invading Farm', 'Objectiveschaden', 'Wardbilanz', 'Gankerfolgrate',
        'Vision Gegnerjungle', 'Gank-Priorität', 'Jungle Proximity');

INSERT INTO input (input_category, input_value1, input_value2, input_value3, input_value4, input_value5, input_value6,
                   input_value7, input_value8)
VALUES ('Middle Playerstats', 'Lane lead', 'Gold pro Minute', 'Creep Score pro Minute', 'Spellbilanz', 'Roams',
        'Wards beschützt', 'Teamschaden', 'Solokills');

INSERT INTO input (input_category, input_value1, input_value2, input_value3, input_value4, input_value5, input_value6,
                   input_value7, input_value8)
VALUES ('Bottom Playerstats', 'Lane lead', 'Gold pro Minute', 'Creep Score pro Minute', 'Early Creep Score',
        'Divebilanz', 'Turret Zeitbilanz', 'Teamschaden', 'Levelup Allins');

INSERT INTO input (input_category, input_value1, input_value2, input_value3, input_value4, input_value5, input_value6,
                   input_value7, input_value8)
VALUES ('Utility Playerstats', 'Lane lead', 'Visionscore Lead', 'Immobilisationen', 'Wardbilanz', 'Roams',
        'Vision Gegnerjungle', 'Teamtankyness', 'Killbeteiligung');

INSERT INTO input (input_category, input_value1, input_value2, input_value3, input_value4, input_value5, input_value6,
                   input_value7, input_value8, input_value9, input_value10)
VALUES ('Teamstats', '', '', '', '', '', '', '', '', '', '');

INSERT INTO input (input_category, input_value1, input_value2)
VALUES ('Top', 'TRUE Seven', 'beatmeattillyeet');

INSERT INTO input (input_category, input_value1, input_value2)
VALUES ('Jungle', 'ThorDerBabo', 'Reflexzi');

INSERT INTO input (input_category, input_value1, input_value2)
VALUES ('Middle', 'TRUE Zoeasy', 'XxBeatZx');

INSERT INTO input (input_category, input_value1, input_value2)
VALUES ('Bottom', 'TRUE Xeri', 'burgerflipper95');

INSERT INTO input (input_category, input_value1, input_value2)
VALUES ('Utility', 'TRUE Whitelizard', 'Serghei');