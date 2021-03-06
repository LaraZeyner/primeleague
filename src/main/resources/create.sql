USE trues;

CREATE TABLE `season`
(
    season_id    SMALLINT(5) UNSIGNED PRIMARY KEY,
    season_name  VARCHAR(21) NOT NULL UNIQUE,
    season_start DATE        NOT NULL UNIQUE,
    season_end   DATE        NOT NULL UNIQUE,
    CHECK ( season_start < season_end )
);

CREATE TABLE `stage`
(
    stage_id    SMALLINT(2) AUTO_INCREMENT PRIMARY KEY,
    season      SMALLINT(5) UNSIGNED NOT NULL,
    stage_type  VARCHAR(18)          NOT NULL,
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
    stage       SMALLINT(2) NOT NULL,
    league_name VARCHAR(25) NOT NULL,
    FOREIGN KEY (stage) REFERENCES `stage` (stage_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE INDEX idx_league_name (stage ASC, league_name ASC)
);

CREATE TABLE `matchday`
(
    matchday_id    SMALLINT(4) AUTO_INCREMENT PRIMARY KEY,
    stage          SMALLINT(2) NOT NULL,
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
    team_tId    INTEGER(6) UNSIGNED NULL UNIQUE,
    team_name   VARCHAR(100)        NOT NULL,
    team_abbr   VARCHAR(25)         NOT NULL,
    team_result VARCHAR(30)         NULL,
    scrims      BOOLEAN             NULL
);

CREATE TABLE `league_team`
(
    league SMALLINT(5) UNSIGNED NOT NULL,
    team   SMALLINT(4)          NOT NULL,
    PRIMARY KEY (league, team),
    FOREIGN KEY (league) REFERENCES `league` (league_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (team) REFERENCES `team` (team_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE `resource`
(
    resource_name VARCHAR(12) PRIMARY KEY
);

CREATE TABLE `champion`
(
    champion_id       SMALLINT(4) PRIMARY KEY,
    champion_name     VARCHAR(16)            NOT NULL UNIQUE,
    champion_title    VARCHAR(30)            NOT NULL UNIQUE,
    subclass          VARCHAR(14)            NULL,
    resource          VARCHAR(16)            NOT NULL,
    attack            TINYINT(2) UNSIGNED    NOT NULL,
    defense           TINYINT(2) UNSIGNED    NOT NULL,
    spell             TINYINT(2) UNSIGNED    NOT NULL,
    health            SMALLINT(4) UNSIGNED   NOT NULL,
    secondary         SMALLINT(5) UNSIGNED   NOT NULL,
    move_speed        SMALLINT(3) UNSIGNED   NOT NULL,
    resist            DECIMAL(9, 6) UNSIGNED NOT NULL,
    attack_range      SMALLINT(4) UNSIGNED   NOT NULL,
    health_regen      DECIMAL(9, 6) UNSIGNED NOT NULL,
    spell_regen       DECIMAL(9, 6) UNSIGNED NOT NULL,
    damage            TINYINT(3) UNSIGNED    NOT NULL,
    attack_speed      DECIMAL(9, 8) UNSIGNED NOT NULL,
    fight_type        VARCHAR(9)             NULL,
    fight_style       VARCHAR(4)             NULL,
    waveclear         TINYINT(2) UNSIGNED    NULL,
    allin             TINYINT(2) UNSIGNED    NOT NULL CHECK ( allin <= 10 )   DEFAULT 5,
    sustain           TINYINT(2) UNSIGNED    NOT NULL CHECK ( sustain <= 10 ) DEFAULT 5,
    trade             TINYINT(2) UNSIGNED    NOT NULL CHECK ( trade <= 10 )   DEFAULT 5,
    playstyle_overall VARCHAR(9)             NULL,
    playstyle_early   VARCHAR(9)             NULL,
    playstyle_pre_6   VARCHAR(9)             NULL,
    playstyle_post_6  VARCHAR(9)             NULL,
    playstyle_mid     VARCHAR(9)             NULL,
    playstyle_late    VARCHAR(9)             NULL,
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
    game_id   VARCHAR(16) PRIMARY KEY,
    queuetype VARCHAR(7) NOT NULL
);

CREATE TABLE `game`
(
    game_id        VARCHAR(16) PRIMARY KEY,
    turnamentmatch INTEGER(7) UNSIGNED  NULL,
    game_start     TIMESTAMP            NOT NULL,
    duration       SMALLINT(4) UNSIGNED NOT NULL CHECK ( duration > 100 ),
    gametype       SMALLINT(4)          NOT NULL,
    FOREIGN KEY (turnamentmatch) REFERENCES `turnament_match` (match_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (gametype) REFERENCES `gametype` (gametype_id)
        ON DELETE RESTRICT ON UPDATE RESTRICT
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
    teamperformance_id    INTEGER(7) AUTO_INCREMENT PRIMARY KEY,
    game                  VARCHAR(16)          NOT NULL,
    team                  SMALLINT(4)          NULL,                             -- not NULL = team round (custom, clash as 3+ or others as 5)
    first_pick            BOOLEAN              NOT NULL,
    win                   BOOLEAN              NOT NULL,
    total_damage          INTEGER(6) UNSIGNED  NOT NULL,
    total_damage_taken    INTEGER(6) UNSIGNED  NOT NULL,
    total_gold            INTEGER(6) UNSIGNED  NOT NULL,
    total_cs              SMALLINT(4) UNSIGNED NOT NULL,
    total_kills           TINYINT(3) UNSIGNED  NOT NULL,
    towers                TINYINT(2) UNSIGNED  NOT NULL CHECK ( towers <= 11 ),
    drakes                TINYINT(2) UNSIGNED  NOT NULL,
    inhibs                TINYINT(2) UNSIGNED  NOT NULL,
    heralds               TINYINT(1) UNSIGNED  NOT NULL CHECK ( heralds <= 2 ),
    barons                TINYINT(2) UNSIGNED  NOT NULL,
    first_tower           BOOLEAN              NOT NULL,
    first_drake           BOOLEAN              NOT NULL,
    perfect_soul          BOOLEAN              NULL,
    rift_turrets          DECIMAL(2, 1)        NULL CHECK ( rift_turrets <= 5 ), -- divided with 10
    elder_time            SMALLINT(4) UNSIGNED NULL,
    baron_powerplay       SMALLINT(4) UNSIGNED NULL,
    surrender             BOOLEAN              NOT NULL,
    ace_before_15         TINYINT(2)           NULL,
    baron_time            SMALLINT(4)          NULL,
    dragon_time           SMALLINT(4)          NULL,
    objective_onspawn     TINYINT(2)           NULL,
    objective_contest     TINYINT(2)           NULL,
    support_quest         BOOLEAN              NULL,
    inhibitors_time       SMALLINT(4)          NULL,
    ace_flawless          TINYINT(1) UNSIGNED  NULL,
    rift_multiturret      TINYINT(1) UNSIGNED  NULL,
    ace_fastest           SMALLINT(4) UNSIGNED NULL,
    kills_deficit         TINYINT(3)           NULL,
    dragon_soul           VARCHAR(8)           NULL,
    team_damage_mitigated INTEGER(7) UNSIGNED  NOT NULL,
    team_immobilizations  SMALLINT(3) UNSIGNED NULL,
    team_vision           SMALLINT(4) UNSIGNED NOT NULL,
    jungletime_wasted     SMALLINT(3) UNSIGNED NULL,
    jungle_path           INTEGER(9) UNSIGNED  NULL,
    FOREIGN KEY (game) REFERENCES `game` (game_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (team) REFERENCES `team` (team_id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    FOREIGN KEY (jungle_path) REFERENCES `path` (path_id)
        ON DELETE SET NULL ON UPDATE CASCADE,
    UNIQUE INDEX idx_teamperformance_side (game ASC, first_pick DESC),
    UNIQUE INDEX idx_teamperformance_win (game ASC, win DESC)
);

CREATE TABLE `path`
(
    path_id    INTEGER(9) AUTO_INCREMENT PRIMARY KEY,
    path_name  VARCHAR(109)         NOT NULL,
    minute_2_x SMALLINT(5) UNSIGNED NULL,
    minute_2_y SMALLINT(5) UNSIGNED NULL,
    minute_3_x SMALLINT(5) UNSIGNED NULL,
    minute_3_y SMALLINT(5) UNSIGNED NULL,
    minute_4_x SMALLINT(5) UNSIGNED NULL,
    minute_4_y SMALLINT(5) UNSIGNED NULL,
    minute_5_x SMALLINT(5) UNSIGNED NULL,
    minute_5_y SMALLINT(5) UNSIGNED NULL,
    minute_6_x SMALLINT(5) UNSIGNED NULL,
    minute_6_y SMALLINT(5) UNSIGNED NULL,
    minute_7_x SMALLINT(5) UNSIGNED NULL,
    minute_7_y SMALLINT(5) UNSIGNED NULL
);

CREATE TABLE `player`
(
    player_id   INTEGER(7) UNSIGNED PRIMARY KEY,
    player_name VARCHAR(100) NULL UNIQUE,
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
    account_id     INTEGER(6) UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    puuid          VARCHAR(78)          NULL UNIQUE,
    summoner_id    VARCHAR(75)          NULL UNIQUE,
    account_name   VARCHAR(25)          NOT NULL,
    player         INTEGER(7) UNSIGNED  NULL,
    icon           SMALLINT(5)          NULL,
    account_level  SMALLINT(4) UNSIGNED NULL,
    account_active BOOLEAN              NULL,
    last_update    TIMESTAMP            NULL,
    FOREIGN KEY (player) REFERENCES `player` (player_id)
        ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE `season_elo`
(
    account INTEGER(6) UNSIGNED  NOT NULL,
    season  SMALLINT(5) UNSIGNED NOT NULL,
    mmr     SMALLINT(4) UNSIGNED NOT NULL DEFAULT 25,
    wins    SMALLINT(4) UNSIGNED NOT NULL,
    losses  SMALLINT(4) UNSIGNED NOT NULL,
    PRIMARY KEY (account, season),
    FOREIGN KEY (account) REFERENCES `account` (account_id)
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
    item_subtype      VARCHAR(15)   NULL,
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

CREATE TABLE `playerperformance_item`
(/**/
    playerperformance_item_id INTEGER(7) UNSIGNED PRIMARY KEY AUTO_INCREMENT,
    playerperformance         INTEGER(7)           NOT NULL,
    buy_timestamp             INTEGER(7) UNSIGNED  NOT NULL,
    item                      SMALLINT(4) UNSIGNED NOT NULL,
    item_remains              BOOLEAN              NOT NULL,
    FOREIGN KEY (playerperformance) REFERENCES `playerperformance` (playerperformance_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (item) REFERENCES `item` (item_id)
        ON DELETE RESTRICT ON UPDATE CASCADE
);

CREATE TABLE `playerperformance`
(
    playerperformance_id                INTEGER(8) AUTO_INCREMENT PRIMARY KEY,
    teamperformance                     INTEGER(6)           NOT NULL,
    account                             INTEGER(6) UNSIGNED  NOT NULL,
    lane                                VARCHAR(8)           NOT NULL,
    champion_own                        SMALLINT(4)          NOT NULL,
    champion_enemy                      SMALLINT(4)          NULL,
    q_usages                            SMALLINT(4) UNSIGNED NOT NULL, -- 1301
    w_usages                            SMALLINT(4) UNSIGNED NOT NULL, -- 545
    e_usages                            SMALLINT(4) UNSIGNED NOT NULL, -- 815
    r_usages                            SMALLINT(3) UNSIGNED NOT NULL, -- 585
    spells_hit                          SMALLINT(4) UNSIGNED NULL,     -- 652
    spells_dodged                       SMALLINT(4) UNSIGNED NULL,     -- 995
    quick_dodged                        SMALLINT(4) UNSIGNED NULL,     -- 796
    damage_magical                      INTEGER(6) UNSIGNED  NOT NULL, -- 134629
    damage_physical                     INTEGER(6) UNSIGNED  NOT NULL, -- 95709
    damage_total                        INTEGER(6) UNSIGNED  NOT NULL, -- 137957
    damage_taken                        INTEGER(6) UNSIGNED  NOT NULL, -- 134720
    damage_mitigated                    INTEGER(6) UNSIGNED  NOT NULL,
    damage_healed                       INTEGER(6) UNSIGNED  NOT NULL,
    damage_shielded                     INTEGER(6) UNSIGNED  NOT NULL,
    kills                               TINYINT(3) UNSIGNED  NOT NULL,
    deaths                              TINYINT(3) UNSIGNED  NOT NULL,
    assists                             TINYINT(3) UNSIGNED  NOT NULL,
    kills_solo                          TINYINT(2) UNSIGNED  NULL,
    allin_levelup                       TINYINT(1) UNSIGNED  NULL CHECK ( allin_levelup < 18 ),
    kills_multi_double                  TINYINT(2) UNSIGNED  NOT NULL,
    kills_multi_triple                  TINYINT(2) UNSIGNED  NOT NULL,
    kills_multi_quadra                  TINYINT(2) UNSIGNED  NOT NULL,
    kills_multi_penta                   TINYINT(2) UNSIGNED  NOT NULL,
    flash_aggressive                    TINYINT(1) UNSIGNED  NULL,
    time_alive                          SMALLINT(4) UNSIGNED NOT NULL,
    time_dead                           SMALLINT(4) UNSIGNED NOT NULL, -- Percentage calc
    kills_teleport                      TINYINT(2) UNSIGNED  NULL,
    immobilizations                     TINYINT(3) UNSIGNED  NULL,
    wards_control                       TINYINT(2) UNSIGNED  NOT NULL,
    wards_control_coverage              TINYINT(3) UNSIGNED  NULL,
    wards_placed                        TINYINT(3) UNSIGNED  NOT NULL,
    wards_cleared                       TINYINT(3) UNSIGNED  NOT NULL,
    wards_guarded                       TINYINT(2) UNSIGNED  NULL,
    visionscore                         TINYINT(3) UNSIGNED  NOT NULL,
    visionscore_advantage               TINYINT(3)           NULL,
    objectives_stolen                   TINYINT(1) UNSIGNED  NOT NULL,
    firstturret_advantage               SMALLINT(4)          NULL,
    objectives_damage                   INTEGER(6) UNSIGNED  NOT NULL,
    baron_executes                      TINYINT(1) UNSIGNED  NULL,
    baron_kills                         TINYINT(1) UNSIGNED  NOT NULL,
    buffs_stolen                        TINYINT(2) UNSIGNED  NULL,
    scuttles_initial                    TINYINT(1) UNSIGNED  NULL CHECK ( scuttles_initial <= 2),
    scuttles_total                      TINYINT(2) UNSIGNED  NULL,
    turrets_splitpushed                 TINYINT(2) UNSIGNED  NULL CHECK ( turrets_splitpushed <= 11 ),
    team_invading                       TINYINT(2) UNSIGNED  NULL,
    ganks_early                         TINYINT(2) UNSIGNED  NULL,
    ganks_total                         TINYINT(2) UNSIGNED  NOT NULL,
    ganks_top                           TINYINT(2) UNSIGNED  NOT NULL,
    ganks_mid                           TINYINT(2) UNSIGNED  NOT NULL,
    ganks_bot                           TINYINT(2) UNSIGNED  NOT NULL,
    dives_done                          TINYINT(2) UNSIGNED  NULL,
    dives_successful                    TINYINT(2) UNSIGNED  NULL,
    dives_gotten                        TINYINT(2) UNSIGNED  NULL,
    dives_protected                     TINYINT(2) UNSIGNED  NULL,
    gold_total                          SMALLINT(5) UNSIGNED NOT NULL,
    gold_bounty                         SMALLINT(4) UNSIGNED NULL,
    experience_total                    SMALLINT(5) UNSIGNED NOT NULL,
    creeps_total                        SMALLINT(4) UNSIGNED NOT NULL,
    creeps_early                        TINYINT(3) UNSIGNED  NULL,
    creeps_invade                       TINYINT(3) UNSIGNED  NOT NULL,
    early_lane_lead                     SMALLINT(5)          NULL,
    lane_lead                           SMALLINT(5)          NULL,
    turretplates                        TINYINT(2) UNSIGNED  NULL CHECK ( turretplates <= 15 ),
    flamehorizon_advantage              SMALLINT(3)          NULL,
    items_amount                        TINYINT(3) UNSIGNED  NOT NULL,
    mejais_completed                    SMALLINT(4) UNSIGNED NULL,
    first_blood                         BOOLEAN              NOT NULL,
    outplayed_opponent                  TINYINT(2) UNSIGNED  NULL,
    turret_takedowns                    TINYINT(2) UNSIGNED  NOT NULL,
    dragon_takedowns                    TINYINT(1) UNSIGNED  NULL,
    fastest_legendary                   SMALLINT(4) UNSIGNED NULL,
    gank_setups                         TINYINT(1) UNSIGNED  NULL,
    buffs_initial                       TINYINT(1) UNSIGNED  NULL,
    kills_early                         TINYINT(2) UNSIGNED  NULL,
    objective_junglerkill               TINYINT(1) UNSIGNED  NULL,
    ambush_kill                         TINYINT(2) UNSIGNED  NULL,
    turrets_early                       TINYINT(1) UNSIGNED  NULL,
    experience_advantage                TINYINT(1) UNSIGNED  NULL,
    pick_kill                           TINYINT(2) UNSIGNED  NULL,
    assassination                       TINYINT(2) UNSIGNED  NULL,
    guard_ally                          TINYINT(2) UNSIGNED  NULL,
    survived_close                      TINYINT(2) UNSIGNED  NULL,
    objectives_stolen_contested         DECIMAL(9, 7)        NULL,
    objectives_killed_jungler_before    DECIMAL(9, 7)        NULL,
    objectives_baron_attempts           DECIMAL(9, 7)        NULL,
    trinket_swap_first                  SMALLINT(4) UNSIGNED NULL,
    ward_placed_first                   SMALLINT(4) UNSIGNED NULL,
    ward_placed_first_control           SMALLINT(4) UNSIGNED NULL,
    ward_control_inventory_time         SMALLINT(4) UNSIGNED NULL,
    turret_participation                DECIMAL(9, 7)        NULL,
    invading_buffs                      DECIMAL(9, 7)        NULL,
    dives_own_rate                      DECIMAL(9, 7)        NULL,
    dives_enemy_rate                    DECIMAL(9, 7)        NULL,
    dives_died                          TINYINT(2) UNSIGNED  NULL,
    team_damage_share                   DECIMAL(9, 7)        NULL,
    team_damage_taken                   DECIMAL(9, 7)        NULL,
    team_damage_mitigated               DECIMAL(9, 7)        NULL,
    bounty_difference                   SMALLINT(4)          NULL,
    duel_win_rate                       DECIMAL(9, 7)        NULL,
    duel_wins                           TINYINT(2) UNSIGNED  NULL,
    ahead                               BOOLEAN              NULL,
    kd_early                            TINYINT(2)           NULL,
    deaths_early                        TINYINT(2) UNSIGNED  NULL,
    behind                              BOOLEAN              NULL,
    ahead_extend                        BOOLEAN              NULL,
    comeback                            BOOLEAN              NULL,
    xp_advantage                        SMALLINT(5)          NULL,
    early_aces_clean                    TINYINT(2) UNSIGNED  NULL,
    first_full_item                     SMALLINT(4) UNSIGNED NULL,
    efficiency_cs_early                 DECIMAL(9, 7)        NULL,
    cs_per_minute                       DECIMAL(9, 6)        NULL,
    xp_per_minute                       DECIMAL(9, 5)        NULL,
    gold_per_minute                     DECIMAL(9, 5)        NULL,
    cs_advantage                        SMALLINT(3) UNSIGNED NULL,
    legendarys_amount                   TINYINT(2) UNSIGNED  NULL,
    grievous_wounds_time                SMALLINT(4) UNSIGNED NULL,
    penetration_time                    SMALLINT(4) UNSIGNED NULL,
    amplifier_time                      SMALLINT(4) UNSIGNED NULL,
    start_item_sold                     SMALLINT(4) UNSIGNED NULL,
    time_alive_percentage               DECIMAL(9, 7)        NULL,
    kills_solo_advantage                TINYINT(2)           NULL,
    first_kill_time                     SMALLINT(4) UNSIGNED NULL,
    first_kill_death_time               SMALLINT(4)          NULL,
    gold_lead_early                     SMALLINT(5)          NULL,
    objectives_advantage_early          TINYINT(2)           NULL,
    objectives_taken_early              TINYINT(2) UNSIGNED  NULL,
    turretplate_advantage               TINYINT(2)           NULL,
    enemy_under_control_advantage       DECIMAL(9, 4)        NULL,
    enemy_under_control                 DECIMAL(9, 4)        NULL,
    keyspells_used                      SMALLINT(4) UNSIGNED NULL,
    spell_bilance                       DECIMAL(9, 7)        NULL,
    hit_bilance                         DECIMAL(9, 7)        NULL,
    dodge_bilance                       DECIMAL(9, 7)        NULL,
    reaction_bilance                    SMALLINT(4)          NULL,
    enemy_reaction                      SMALLINT(4) UNSIGNED NULL,
    lead_diff_after_death_early         SMALLINT(5)          NULL,
    kill_participation                  DECIMAL(9, 7)        NULL,
    trueKdaValue                        DECIMAL(9, 6)        NULL,
    kda_true_kills                      DECIMAL(9, 6)        NULL,
    kda_true_deaths                     DECIMAL(9, 6)        NULL,
    kda_true_assists                    DECIMAL(9, 6)        NULL,
    enemy_early_under_control_advantage DECIMAL(9, 4)        NULL,
    enemy_early_under_control           DECIMAL(9, 4)        NULL,
    farm_drop_minute                    SMALLINT(3) UNSIGNED NULL,
    trinket_efficiency                  DECIMAL(9, 7)        NULL,
    goldxp_efficiency_midgame           DECIMAL(9, 7)        NULL,
    gold_efficiency_midgame             DECIMAL(9, 7)        NULL,
    durability_time                     SMALLINT(4) UNSIGNED NULL,
    lead_lategame                       SMALLINT(5)          NULL,
    behind_farm                         SMALLINT(4)          NULL,
    behind_warding                      SMALLINT(3)          NULL,
    behind_deaths                       SMALLINT(3)          NULL,
    behind_gold                         SMALLINT(5)          NULL,
    behind_xp                           SMALLINT(5)          NULL,
    levelup_lead                        DECIMAL(9, 7)        NULL,
    pick_advantage                      TINYINT(2)           NULL,
    teamfight_amount                    TINYINT(2) UNSIGNED  NULL,
    teamfight_participation             DECIMAL(9, 7)        NULL,
    death_order_average                 DECIMAL(9, 7)        NULL,
    teamfight_winrate                   DECIMAL(9, 7)        NULL,
    teamfight_damage_rate               DECIMAL(9, 7)        NULL,
    skirmish_amount                     SMALLINT(3) UNSIGNED NULL,
    skirmish_participation              DECIMAL(9, 7)        NULL,
    skirmish_kills                      DECIMAL(9, 8)        NULL,
    skirmish_winrate                    DECIMAL(9, 7)        NULL,
    skirmish_damage_rate                DECIMAL(9, 7)        NULL,
    roam_cs_advantage                   TINYINT(2)           NULL,
    roam_goldxp_advantage               SMALLINT(4)          NULL,
    roam_gold_advantage                 SMALLINT(4)          NULL,
    roam_objectivedamage_advantage      SMALLINT(4)          NULL,
    roam_successscore                   SMALLINT(5)          NULL,
    death_positioning_relative          DECIMAL(9, 7)        NULL,
    positioning_lane                    DECIMAL(9, 7)        NULL,
    positioning_mid                     DECIMAL(9, 7)        NULL,
    positioning_late                    DECIMAL(9, 7)        NULL,
    killdeath_positioning_lane          DECIMAL(9, 7)        NULL,
    kill_positioning_lane               DECIMAL(9, 7)        NULL,
    positioning_split_score             INTEGER(7) UNSIGNED  NULL,
    positioning_companion_score         INTEGER(7) UNSIGNED  NULL,
    positioning_roam_score              INTEGER(7) UNSIGNED  NULL,
    time_combat                         SMALLINT(4) UNSIGNED NULL,
    base_first_time                     SMALLINT(4) UNSIGNED NULL,
    base_first_recall                   BOOLEAN              NULL,
    lead_through_deaths                 SMALLINT(5)          NULL,
    base_first_controlled               SMALLINT(4) UNSIGNED NULL,
    base_first_lead                     SMALLINT(4)          NULL,
    base_first_gold                     SMALLINT(4) UNSIGNED NULL,
    base_first_gold_unspent             SMALLINT(4)          NULL,
    base_recall                         DECIMAL(9, 7)        NULL,
    base_planned                        DECIMAL(9, 7)        NULL,
    base_total                          SMALLINT(3) UNSIGNED NULL,
    base_duration                       INTEGER(7) UNSIGNED  NULL,
    base_gold                           SMALLINT(5) UNSIGNED NULL,
    base_gold_unspent                   SMALLINT(5) UNSIGNED NULL,
    base_gold_lost                      SMALLINT(4)          NULL,
    base_together                       DECIMAL(9, 7)        NULL,
    base_second_time                    SMALLINT(4) UNSIGNED NULL,
    base_consumables_purchased          BOOLEAN              NULL,
    base_resource_conservation          DECIMAL(9, 7)        NULL,
    base_second_controlled              SMALLINT(4) UNSIGNED NULL,
    damage_early_percentage             DECIMAL(9, 7)        NULL,
    wards_earlygame                     SMALLINT(3) UNSIGNED NULL,
    xp_early                            DECIMAL(9, 7)        NULL,
    damage_early_difference             SMALLINT(5)          NULL,
    lane_health                         DECIMAL(9, 7)        NULL,
    lane_resource                       DECIMAL(9, 7)        NULL,
    wave_status_push                    TINYINT(2)           NULL,
    wave_status_freeze                  TINYINT(2)           NULL,
    wave_status_hold                    TINYINT(2)           NULL,
    utility_score                       DECIMAL(9, 7)        NULL,
    lead_without_dying                  SMALLINT(5)          NULL,
    proximity                           DECIMAL(9, 6)        NULL,
    lane_proximity                      DECIMAL(9, 6)        NULL,
    FOREIGN KEY (teamperformance) REFERENCES `teamperformance` (teamperformance_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (account) REFERENCES `account` (account_id)
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
    summonerspell     TINYINT(2) UNSIGNED NOT NULL,
    usages            TINYINT(2) UNSIGNED NOT NULL,
    PRIMARY KEY (playerperformance, summonerspell),
    FOREIGN KEY (playerperformance) REFERENCES `playerperformance` (playerperformance_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (summonerspell) REFERENCES `summonerspell` (summonerspell_id)
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
    playerperformance     INTEGER(7)             NOT NULL,
    info_minute           TINYINT(3) UNSIGNED    NOT NULL,
    info_gold_total       SMALLINT(5) UNSIGNED   NOT NULL,
    info_gold_current     SMALLINT(5) UNSIGNED   NOT NULL,
    enemy_controlled      DECIMAL(9, 4) UNSIGNED NOT NULL,
    position_x            SMALLINT(5) UNSIGNED   NOT NULL,
    position_y            SMALLINT(5) UNSIGNED   NOT NULL,
    info_experience       SMALLINT(5) UNSIGNED   NOT NULL,
    info_lead             SMALLINT(5)            NOT NULL,
    info_creep_score      SMALLINT(4) UNSIGNED   NOT NULL,
    info_damage_total     INTEGER(6) UNSIGNED    NOT NULL,
    info_health_max       SMALLINT(5) UNSIGNED   NOT NULL,
    info_health_current   SMALLINT(5) UNSIGNED   NOT NULL,
    info_resource_max     SMALLINT(5) UNSIGNED   NOT NULL,
    info_resource_current SMALLINT(5) UNSIGNED   NOT NULL,
    info_movespeed        SMALLINT(4) UNSIGNED   NOT NULL,
    PRIMARY KEY (playerperformance, info_minute),
    FOREIGN KEY (playerperformance) REFERENCES `playerperformance` (playerperformance_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE `rating`
(
    rating_category VARCHAR(25) NOT NULL,
    rating_type     VARCHAR(8)  NOT NULL,
    rating_subtype  VARCHAR(9)  NOT NULL,
    rating_value    SMALLINT(3) NOT NULL DEFAULT 300 CHECK ( rating_value <= 750 ),
    PRIMARY KEY (rating_category, rating_type, rating_subtype)
);

CREATE TABLE `absence`
(
    absence_id         SMALLINT(5) PRIMARY KEY AUTO_INCREMENT,
    player             INTEGER(7) UNSIGNED NOT NULL,
    absence_start_time TIMESTAMP           NOT NULL,
    absence_end_time   TIMESTAMP           NULL,
    absence_type       VARCHAR(12)         NOT NULL
);

CREATE TABLE team_member
(
    member_id     TINYINT(3) PRIMARY KEY AUTO_INCREMENT,
    member_name   VARCHAR(25)         NOT NULL,
    member_status VARCHAR(8)          NOT NULL,
    player        INTEGER(7) UNSIGNED NULL,
    FOREIGN KEY (player) REFERENCES `player` (player_id)
        ON DELETE SET NULL ON UPDATE SET NULL,
    UNIQUE INDEX idx_teammember_name (member_name ASC)
);
