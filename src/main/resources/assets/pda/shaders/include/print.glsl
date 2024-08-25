/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

#ifdef __ssbo_support
#extension GL_ARB_shader_storage_buffer_object : require
#endif//__sbo_support

special const int PRINT_BUFFER_SIZE = 1;

#ifdef __ssbo_support
layout(std430) buffer PrintBuffer {
    uint data[PRINT_BUFFER_SIZE];
};
#endif//__ssbo_support