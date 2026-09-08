package com.tracker.syllabus.data.model

object SyllabusSeedData {
    fun getSeedSubjects(): List<Subject> {
        return listOf(
            Subject("indian_polity", "Indian Polity", "Constitution, governance, political system, Panchayati Raj, public policy, and rights issues."),
            Subject("indian_economy", "Economics", "Economic development, growth, planning, monetary system, public finance, agriculture, inflation, and external sector."),
            Subject("geography", "Geography", "Physical, social, and economic geography of India and the world."),
            Subject("indian_history", "Indian History", "Ancient, Medieval, and Modern Indian History, along with Indian National Movement, Art, and Culture."),
            Subject("env_ecology", "Environment & Ecology", "Biodiversity, climate change, environmental conservation, pollution, and national/international environmental frameworks."),
            Subject("science_tech", "Science & Technology", "Space, biotechnology, IT, defense, nanotechnology, and developments in everyday science.")
        )
    }

    fun getSeedTopics(): List<Topic> {
        return listOf(
            // Indian Polity
            Topic("polity_historical_background", "indian_polity", "Historical Background & Making of the Constitution"),
            Topic("polity_features_preamble", "indian_polity", "Salient Features & Preamble of the Constitution"),
            Topic("polity_union_citizenship", "indian_polity", "Union & its Territory & Citizenship"),
            Topic("polity_fundamental_rights", "indian_polity", "Fundamental Rights"),
            Topic("polity_dpsp_fd", "indian_polity", "Directive Principles (DPSP) & Fundamental Duties"),
            Topic("polity_amendment_structure", "indian_polity", "Amendment of Constitution & Basic Structure"),
            Topic("polity_president_vp", "indian_polity", "President, Vice-President, and Emergency Provisions"),
            Topic("polity_pm_council", "indian_polity", "Prime Minister & Central Council of Ministers"),
            Topic("polity_parliament", "indian_polity", "Parliament (Lok Sabha, Rajya Sabha & Procedures)"),
            Topic("polity_judiciary", "indian_polity", "Supreme Court, High Courts, and Judicial Review"),
            Topic("polity_state_gov", "indian_polity", "Governor, Chief Minister & State Legislature"),
            Topic("polity_local_gov", "indian_polity", "Panchayati Raj & Municipalities (73rd & 74th Amendments)"),
            Topic("polity_constitutional_bodies", "indian_polity", "Constitutional Bodies (Election Comm, UPSC, CAG)"),
            Topic("polity_non_constitutional", "indian_polity", "Non-Constitutional Bodies (NITI Aayog, NHRC, CIC)"),

            // Economics
            Topic("econ_national_income", "indian_economy", "National Income, GDP, GNP & Economic Growth"),
            Topic("econ_inflation", "indian_economy", "Inflation: Concepts, Types, and Control Measures"),
            Topic("econ_monetary_policy", "indian_economy", "RBI, Monetary Policy, and Indian Banking System"),
            Topic("econ_fiscal_policy", "indian_economy", "Public Finance, Union Budget & Fiscal Policy"),
            Topic("econ_taxation", "indian_economy", "Taxation Structure in India & GST"),
            Topic("econ_poverty_unemployment", "indian_economy", "Poverty, Inequality, and Unemployment in India"),
            Topic("econ_agriculture", "indian_economy", "Indian Agriculture: Land Reforms, Subsidies & Food Security"),
            Topic("econ_industry_infrastructure", "indian_economy", "Industrial Policies, Infrastructure, and Service Sector"),
            Topic("econ_external_sector", "indian_economy", "Balance of Payments, Foreign Trade, FDI, and WTO"),
            Topic("econ_financial_markets", "indian_economy", "Stock Markets, SEBI, Insurance, and Capital Markets"),

            // Geography
            Topic("geo_geomorphology", "geography", "Geomorphology: Earth's Structure, Plate Tectonics & Earthquakes"),
            Topic("geo_climatology", "geography", "Climatology: Atmosphere, Winds, Cyclones & Rainfall"),
            Topic("geo_oceanography", "geography", "Oceanography: Ocean Relief, Salinity, Currents & Tides"),
            Topic("geo_india_physiography", "geography", "Physiography and Drainage Systems of India"),
            Topic("geo_india_climate_soil", "geography", "Climate, Soils, and Natural Vegetation of India"),
            Topic("geo_resources_agriculture", "geography", "Mineral & Energy Resources and Agriculture Patterns"),
            Topic("geo_industries_transport", "geography", "Industrial Localization & Transport Systems of India"),
            Topic("geo_human_geography", "geography", "Human Geography: Population, Demography & Urbanization"),

            // History
            Topic("hist_ancient_civilizations", "indian_history", "Indus Valley Civilization & Vedic Age"),
            Topic("hist_buddhism_jainism", "indian_history", "Buddhism, Jainism, and Mahajanapadas"),
            Topic("hist_mauryas_guptas", "indian_history", "Mauryan and Gupta Empires"),
            Topic("hist_medieval_sultanate", "indian_history", "Delhi Sultanate & Mughal Empire"),
            Topic("hist_vijayanagara_bhakti", "indian_history", "Vijayanagara Empire, Bhakti & Sufi Movements"),
            Topic("hist_art_culture", "indian_history", "Indian Art, Architecture, Paintings, and Classical Dances"),
            Topic("hist_british_conquest", "indian_history", "British Expansion & Administrative Policies (1757-1856)"),
            Topic("hist_revolt_1857", "indian_history", "Revolt of 1857 & Early Socio-Religious Reforms"),
            Topic("hist_national_movement_early", "indian_history", "Foundation of INC & Moderate/Extremist Phases"),
            Topic("hist_gandhian_era", "indian_history", "Gandhian Movements (NCM, CDM, Quit India)"),
            Topic("hist_partition_independence", "indian_history", "Revolutionary Nationalism, Partition & Independence"),

            // Environment
            Topic("env_ecosystems", "env_ecology", "Ecology & Ecosystems: Concepts, Food Chains & Webs"),
            Topic("env_biodiversity", "env_ecology", "Biodiversity: Hotspots, Threatened Species & Conservation"),
            Topic("env_pollution", "env_ecology", "Environmental Pollution: Air, Water, Soil, Noise & Waste Management"),
            Topic("env_climate_change", "env_ecology", "Climate Change: Greenhouse Effect, Global Warming & Mitigation"),
            Topic("env_laws_conventions", "env_ecology", "Environmental Protection Acts, Wildlife Protection Act & International Conventions"),
            Topic("env_protected_areas", "env_ecology", "National Parks, Wildlife Sanctuaries & Biosphere Reserves"),

            // Science & Tech
            Topic("sci_space_tech", "science_tech", "Space Technology: Orbits, Satellites, ISRO & Space Missions"),
            Topic("sci_biotech", "science_tech", "Biotechnology: DNA, Stem Cells, Cloning & Applications"),
            Topic("sci_it_telecom", "science_tech", "Information Technology, AI, 5G, IoT & Cybersecurity"),
            Topic("sci_defense", "science_tech", "Defense Technology: Missiles, Submarines & Indigenization"),
            Topic("sci_nanotech_nuclear", "science_tech", "Nanotechnology & Nuclear Science (Three-Stage Nuclear Program)")
        )
    }
}
