/*
 * Copyright (C) 2024 Karma Krafts & associates
 */

#include "shader_type.glsl"

#if (BUILTIN_SSBO_SUPPORT == 1)
#if (BUILTIN_GL_MAJOR < 4 || (BUILTIN_GL_MAJOR == 4 && BUILTIN_GL_MINOR < 3))
#if (BUILTIN_SHADER_TYPE == SHADER_TYPE_VERTEX)
#extension GL_ARB_shader_storage_buffer_object : require
#define BUILTIN_PRINT_SUPPORT 1
#else//BUILTIN_SHADER_TYPE
#define BUILTIN_PRINT_SUPPORT 0
#endif//BUILTIN_SHADER_TYPE
#else
#extension GL_ARB_shader_storage_buffer_object : require
#define BUILTIN_PRINT_SUPPORT 1
#endif
#endif//BUILTIN_SSBO_SUPPORT

special const int PRINT_BUFFER_SIZE = 1;

#if (BUILTIN_PRINT_SUPPORT == 1)
layout(std430) buffer PrintBuffer {
    uint data[PRINT_BUFFER_SIZE];
};
#endif//BUILTIN_PRINT_SUPPORT